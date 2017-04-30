package info.novatec.testit.livingdoc.confluence.actions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import info.novatec.testit.livingdoc.confluence.LivingDocServerConfigurationActivator;
import info.novatec.testit.livingdoc.confluence.macros.LivingDocPage;
import info.novatec.testit.livingdoc.confluence.velocity.LivingDocConfluenceManager;
import info.novatec.testit.livingdoc.server.LivingDocServerErrorKey;
import info.novatec.testit.livingdoc.server.LivingDocServerException;
import info.novatec.testit.livingdoc.server.domain.Execution;
import info.novatec.testit.livingdoc.server.domain.Project;
import info.novatec.testit.livingdoc.server.domain.Reference;
import info.novatec.testit.livingdoc.server.domain.Repository;
import info.novatec.testit.livingdoc.server.domain.Requirement;
import info.novatec.testit.livingdoc.server.domain.Runner;
import info.novatec.testit.livingdoc.server.domain.Specification;
import info.novatec.testit.livingdoc.server.domain.SystemUnderTest;
import info.novatec.testit.livingdoc.util.ExceptionUtils;
import info.novatec.testit.livingdoc.util.HtmlUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@SuppressWarnings("serial")
public class SpecificationAction extends AbstractLivingDocAction {
    private static Logger log = LoggerFactory.getLogger(SpecificationAction.class);

    protected Specification specification;

    private List<Reference> references;
    private List<Repository> repositories;
    private List<SystemUnderTest> projectSystemUnderTests;

    private String selectedSystemUnderTestInfo;
    private Execution execution;

    private String requirementName;
    private String sutName;
    private String sutProjectName;
    private String repositoryUid;
    private String sections;

    private boolean isMain;
    private boolean isSutEditable;
    protected boolean implemented;

    public SpecificationAction(){}
    public SpecificationAction(LivingDocConfluenceManager confluenceLivingDoc,
                               LivingDocServerConfigurationActivator livingDocServerConfigurationActivator) {
        super(confluenceLivingDoc, livingDocServerConfigurationActivator);
    }

    public String loadSpecification() {
        try {
            log.debug("retrieving specification ...");
            specification = getLivingDocConfluenceManager().getSpecification(spaceKey, getPage().getTitle());
            log.debug("Specification found : " + (specification == null ? "NONE" : specification.getName()) );

        } catch (LivingDocServerException e) {
            log.error("Error loading specification" , e);
            if ( ! e.getId().equals(LivingDocServerErrorKey.SPECIFICATION_NOT_FOUND))
                addActionError(e);
        }

        return SUCCESS;
    }

    public String updateSelectedSystemUndertTest() {
        getLivingDocConfluenceManager().saveSelectedSystemUnderTestInfo(page, selectedSystemUnderTestInfo);
        return SUCCESS;
    }

    public String getSystemUndertTestSelection() {
        try {
            specification = getLivingDocConfluenceManager().getSpecification(spaceKey, getPage().getTitle());
            projectSystemUnderTests = getLivingDocConfluenceManager().getSystemsUnderTests(spaceKey);
        } catch (LivingDocServerException e) {
            projectSystemUnderTests = new ArrayList<SystemUnderTest>();
            addActionError(e);
        }

        return SUCCESS;
    }

    public String addSystemUnderTest() {
        try {
            SystemUnderTest sut = SystemUnderTest.newInstance(sutName);
            sut.setProject(Project.newInstance(sutProjectName));
            sut.setRunner(Runner.newInstance(""));

            Specification specification = Specification.newInstance(getPage().getTitle());
            specification.setRepository(getLivingDocConfluenceManager().getHomeRepository(spaceKey));

            getLivingDocConfluenceManager().getPersistenceService().addSpecificationSystemUnderTest(sut, specification);
        } catch (LivingDocServerException e) {
            addActionError(e);
        }

        return getSystemUndertTestSelection();
    }

    public String removeSystemUnderTest() {
        try {
            SystemUnderTest sut = SystemUnderTest.newInstance(sutName);
            sut.setProject(Project.newInstance(sutProjectName));
            sut.setRunner(Runner.newInstance(""));

            Specification specification = Specification.newInstance(getPage().getTitle());
            specification.setRepository(getLivingDocConfluenceManager().getHomeRepository(spaceKey));

            getLivingDocConfluenceManager().getPersistenceService().removeSpecificationSystemUnderTest(sut, specification);
        } catch (LivingDocServerException e) {
            addActionError(e);
        }

        return getSystemUndertTestSelection();
    }

    public String run() {
        String locale = getLocale().getLanguage();

        SystemUnderTest sut = SystemUnderTest.newInstance(sutName);
        sut.setProject(Project.newInstance(sutProjectName));
        sut.setRunner(Runner.newInstance(""));

        Specification spec = Specification.newInstance(getPage().getTitle());

        try {
            spec.setRepository(getLivingDocConfluenceManager().getHomeRepository(spaceKey));

            execution = getLivingDocConfluenceManager().getPersistenceService().runSpecification(sut, spec, implemented, locale);
        } catch (LivingDocServerException e) {
            execution = Execution.error(spec, sut, null, e.getId());
        } catch (Exception e) {
            execution = Execution.error(spec, sut, null, ExceptionUtils.stackTrace(e, "<br>", 15));
        }

        return SUCCESS;
    }

    public String retrieveReferenceList() {
        try {
            log.debug("retrieving references ...");
            references = getLivingDocConfluenceManager().getReferences(spaceKey, getPage().getTitle());
            log.debug("References found : "+ (references == null ? "NONE": references.size()));

            if (isEditMode) {
                log.debug("Retrieving repositories ...");
                repositories = getLivingDocConfluenceManager().getRepositories(spaceKey);
                log.debug("Repositories found : "+ (repositories == null ? "NONE": repositories.size()));

                if (repositories.isEmpty())
                    throw new LivingDocServerException("livingdoc.server.repositoriesnotfound", "");

                log.debug("Retrieving SUTs ...");
                projectSystemUnderTests = getLivingDocConfluenceManager().getSystemsUnderTests(spaceKey);
                log.debug("SUTs found : "+ (projectSystemUnderTests == null ? "NONE": projectSystemUnderTests.size()));
                if (projectSystemUnderTests.isEmpty())
                    throw new LivingDocServerException("livingdoc.server.sutsnotfound", "");
            }
        } catch (LivingDocServerException e) {
            addActionError(e);
            isEditMode = false;
        }

        return SUCCESS;
    }

    public String addReference() {
        try {
            getLivingDocConfluenceManager().getPersistenceService().createReference(instanceOfReference());
        } catch (LivingDocServerException e) {
            addActionError(e);
        }

        return retrieveReferenceList();
    }

    public String removeReference() {
        isEditMode = true;

        try {
            getLivingDocConfluenceManager().getPersistenceService().removeReference(instanceOfReference());
        } catch (LivingDocServerException e) {
            addActionError(e);
        }

        return retrieveReferenceList();
    }

    public Specification getSpecification() {
        return specification;
    }

    public List<Repository> getRepositories() {
        return repositories;
    }

    public List<SystemUnderTest> getProjectSystemUnderTests() {
        return projectSystemUnderTests;
    }

    public Set<SystemUnderTest> getSpecificationSystemUnderTests() {
        if (specification == null)
            return new HashSet<SystemUnderTest>();
        return specification.getTargetedSystemUnderTests();
    }

    public boolean getIsSutEditable() {
        return this.isSutEditable;
    }

    public void setIsSutEditable(boolean isSutEditable) {
        this.isSutEditable = isSutEditable;
    }

    public boolean getImplemented() {
        return implemented;
    }

    public void setImplemented(boolean implemented) {
        this.implemented = implemented;
    }

    public String getRequirementName() {
        return requirementName;
    }

    public void setRequirementName(String requirementName) {
        this.requirementName = requirementName;
    }

    public void setSutInfo(String sutInfo) {
        StringTokenizer stk = new StringTokenizer(sutInfo, "@");
        this.sutProjectName = stk.nextToken();
        this.sutName = stk.nextToken();
    }

    public void setSutName(String sutName) {
        this.sutName = sutName;
    }

    public void setSutProjectName(String sutProjectName) {
        this.sutProjectName = sutProjectName;
    }

    public void setRepositoryUid(String repositoryUid) {
        this.repositoryUid = repositoryUid;
    }

    public void setSections(String sections) {
        this.sections = sections.trim();
    }

    public List<Reference> getReferences() {
        return references;
    }

    public SystemUnderTest getSelectedSystemUnderTest() {
        return getLivingDocConfluenceManager().getSelectedSystemUnderTest(page);
    }

    public boolean getIsExecutable() {
        return specification != null;
    }

    public Execution getExecution() {
        return execution;
    }

    public String getSelectedSystemUnderTestInfo() {
        if (selectedSystemUnderTestInfo != null) {
            return selectedSystemUnderTestInfo;
        }
        selectedSystemUnderTestInfo = getLivingDocConfluenceManager().getSelectedSystemUnderTestInfo(page);
        return selectedSystemUnderTestInfo;
    }

    public void setSelectedSystemUnderTestInfo(String selectedSystemUnderTestInfo) {
        this.selectedSystemUnderTestInfo = selectedSystemUnderTestInfo;
    }

    public boolean getIsMain() {
        return isMain;
    }

    public void setIsMain(boolean isMain) {
        this.isMain = isMain;
    }

    public String getRenderedResults() {
        String results = execution.getResults();

        if (results != null) {
            results = results.replaceAll("livingdoc-manage-not-rendered", "livingdoc-manage");
            results = results.replaceAll("livingdoc-hierarchy-not-rendered", "livingdoc-hierarchy");
            results = results.replaceAll("livingdoc-children-not-rendered", "livingdoc-children");
            results = results.replaceAll("livingdoc-labels-not-rendered", "livingdoc-labels");
            results = results.replaceAll("livingdoc-group-not-rendered", "livingdoc-group");
            results = results.replaceAll("livingdoc-page-not-rendered", LivingDocPage.MACRO_KEY);
            results = results.replaceAll("Unknown macro:", "");
            results = removeUnknownMacroElements(results);
            return HtmlUtil.cleanUpResults(results);
        }

        return null;
    }

    public boolean isInSpecificationSelection(SystemUnderTest systemUnderTest) {
        return getLivingDocConfluenceManager().isInSutList(systemUnderTest, specification.getTargetedSystemUnderTests());
    }

    /********************* Utils *********************/

    private Reference instanceOfReference() throws LivingDocServerException {
        SystemUnderTest sut = SystemUnderTest.newInstance(sutName);
        sut.setProject(Project.newInstance(sutProjectName));

        Specification specification = Specification.newInstance(getPage().getTitle());
        specification.setRepository(getLivingDocConfluenceManager().getHomeRepository(spaceKey));

        Requirement requirement = Requirement.newInstance(requirementName);
        requirement.setRepository(Repository.newInstance(repositoryUid));

        return Reference.newInstance(requirement, specification, sut, sections);
    }

    private String removeUnknownMacroElements(String result) {
        Document document = Jsoup.parse(result);
        document.getElementsByClass("wysiwyg-unknown-macro").stream()
            .filter(element -> element.attr("src").contains("livingdoc"))
            .forEach(element -> element.remove());

        return document.html();
    }

    public int getNextFieldId() {
        return getFieldId() + 1;
    }
}
