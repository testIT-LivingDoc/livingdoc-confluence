/**
 * Copyright (c) 2008 Pyxis Technologies inc.
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF site:
 * http://www.fsf.org.
 */
package info.novatec.testit.livingdoc.confluence.macros;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.renderer.radeox.macros.MacroUtils;
import com.atlassian.confluence.util.velocity.VelocityUtils;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.macro.MacroException;

import info.novatec.testit.livingdoc.confluence.macros.historic.AbstractChartBuilder;
import info.novatec.testit.livingdoc.confluence.macros.historic.AggregationExecutionChartBuilder;
import info.novatec.testit.livingdoc.confluence.macros.historic.HistoricParameters;
import info.novatec.testit.livingdoc.confluence.macros.historic.LinearExecutionChartBuilder;
import info.novatec.testit.livingdoc.confluence.utils.MacroCounter;
import info.novatec.testit.livingdoc.server.LivingDocServerException;
import info.novatec.testit.livingdoc.server.domain.Execution;
import info.novatec.testit.livingdoc.server.domain.Specification;
import info.novatec.testit.livingdoc.server.domain.SystemUnderTest;


public class LivingDocHistoric extends AbstractLivingDocMacro {

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public String execute(Map parameters, String body, RenderContext renderContext) throws MacroException {
        try {
            Map contextMap = MacroUtils.defaultVelocityContext();
            String spaceKey = getSpaceKey(parameters, renderContext, false);
            Page page = getPage(parameters, renderContext, spaceKey);

            String executionUID = "LD_HISTORIC_" + MacroCounter.instance().getNextCount();

            HistoricParameters settings = new HistoricParameters(parameters, spaceKey, page, executionUID, ldUtil);

            AbstractChartBuilder chartBuilder;

            if (settings.getLabels() == null && settings.isNoChildren()) {
                Specification specification = ldUtil.getSpecification(page.getSpaceKey(), page.getTitle().trim());

                List<Execution> executions = ldUtil.getPersistenceService().getSpecificationExecutions(specification, settings
                    .getTargetedSystemUnderTest(), settings.getMaxResult());

                chartBuilder = LinearExecutionChartBuilder.newInstance(settings, executions, ldUtil);
            } else {
                List<Specification> specifications = new ArrayList<Specification>();

                if ( ! settings.isNoChildren()) {
                    List<Specification> specificationsChildren = getTargetSpecificationsChildren(page, settings.getSut(),
                        settings.isAllChildren());
                    specifications.addAll(specificationsChildren);
                }

                List<Execution> executions = aggregateExecutions(specifications, settings.getTargetedSystemUnderTest(),
                    settings.getMaxResult());

                chartBuilder = AggregationExecutionChartBuilder.newInstance(settings, executions, ldUtil);
            }

            String chartMapId = executionUID + "_map";

            contextMap.put("executionUID", executionUID);
            contextMap.put("chartImage", chartBuilder.generateChart());
            contextMap.put("settings", settings);
            contextMap.put("chartMapHtml", chartBuilder.getChartMap(chartMapId));
            contextMap.put("chartMapId", "#" + chartMapId);

            return VelocityUtils.getRenderedTemplate("/templates/livingdoc/confluence/macros/livingDocHistoric.vm",
                contextMap);
        } catch (LivingDocServerException lde) {
            return getErrorView("livingdoc.historic.macroid", lde.getId());
        } catch (Exception e) {
            return getErrorView("livingdoc.historic.macroid", e.getMessage());
        }
    }

    private List<Specification> getTargetSpecificationsChildren(final Page page, String sut, boolean allChildren)
        throws LivingDocServerException {
        List<Page> pages = new ArrayList<Page>() {
            private static final long serialVersionUID = 1L;

            {
                add(page);
            }
        };

        fillChildrenPages(page, allChildren, pages);

        return getSpecificationsFromPages(sut, pages);
    }

    private boolean isSpecificationAssociatedToSut(Specification specification, String systemUnderTest) {
        Set<SystemUnderTest> suts = specification.getTargetedSystemUnderTests();

        for (SystemUnderTest sut : suts) {
            if (sut.getName().equals(systemUnderTest)) {
                return true;
            }
        }

        return false;
    }

    private void fillChildrenPages(Page page, boolean allChildren, List<Page> childrens) {
        List<Page> pageChildren = ldUtil.getContentPermissionManager().getPermittedChildren(page, ldUtil.getRemoteUser());

        for (Page child : pageChildren) {
            if (ldUtil.isExecutable(child)) {
                childrens.add(child);
            }

            if (allChildren) {
                fillChildrenPages(child, allChildren, childrens);
            }
        }
    }

    private void sort(List<Specification> specifications) {
        Collections.sort(specifications, new Comparator<Specification>() {
            @Override
            public int compare(Specification o1, Specification o2) {
                return o1.getName().compareToIgnoreCase(o2.getName());
            }
        });
    }

    protected List<Specification> getSpecificationsFromPages(String sut, List<Page> pages) throws LivingDocServerException {
        List<Specification> specifications = new ArrayList<Specification>();

        for (Page childPage : pages) {
            Specification specification = ldUtil.getSpecification(childPage);

            if (specification != null && isSpecificationAssociatedToSut(specification, sut)) {
                specifications.add(specification);
            }
        }

        sort(specifications);

        return specifications;
    }

    private List<Execution> aggregateExecutions(List<Specification> specifications, SystemUnderTest targetedSystemUnderTest,
        int maxResult) throws LivingDocServerException {
        List<Execution> executions = new ArrayList<Execution>();

        for (Specification specification : specifications) {
            List<Execution> execs = ldUtil.getPersistenceService().getSpecificationExecutions(specification,
                targetedSystemUnderTest, maxResult);

            int failureCount = 0;
            int errorCount = 0;
            int ignoredCount = 0;
            int successCount = 0;

            for (Execution exec : execs) {
                failureCount += exec.getFailures();
                errorCount += exec.getErrors() + ( exec.hasException() ? 1 : 0 );
                ignoredCount += exec.getIgnored();
                successCount += exec.getSuccess();
            }

            Execution execution = new Execution();
            execution.setErrors(errorCount);
            execution.setFailures(failureCount);
            execution.setIgnored(ignoredCount);
            execution.setSuccess(successCount);
            execution.setSpecification(specification);
            execution.setId(specification.getId());
            executions.add(execution);
        }

        return executions;
    }
}
