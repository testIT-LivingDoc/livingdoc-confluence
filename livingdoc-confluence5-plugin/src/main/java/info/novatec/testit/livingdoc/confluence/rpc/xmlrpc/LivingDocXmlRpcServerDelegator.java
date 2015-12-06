/* Copyright (c) 2008 Pyxis Technologies inc.
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
 * http://www.fsf.org. */
package info.novatec.testit.livingdoc.confluence.rpc.xmlrpc;

import java.util.Vector;

import info.novatec.testit.livingdoc.confluence.rpc.RpcClientService;
import info.novatec.testit.livingdoc.confluence.utils.stylesheet.StyleSheetExtractor;
import info.novatec.testit.livingdoc.confluence.velocity.ConfluenceLivingDoc;
import info.novatec.testit.livingdoc.server.rpc.LivingDocRpcHelper;
import info.novatec.testit.livingdoc.server.rpc.xmlrpc.LivingDocXmlRpcServer;


public class LivingDocXmlRpcServerDelegator implements RpcClientService {

    private final LivingDocXmlRpcServer delegator;
    private final LivingDocRpcHelper confluenceServiceDelegator;

    /**
     * Constructor for IoC
     */
    public LivingDocXmlRpcServerDelegator(ConfluenceLivingDoc confluenceLivingDoc,
        LivingDocXmlRpcServer livingDocXmlRpcServer, StyleSheetExtractor styleSheetExtractor) {
        confluenceServiceDelegator = new ConfluenceXmlRpcLivingDocServiceImpl(confluenceLivingDoc, styleSheetExtractor);
        delegator = livingDocXmlRpcServer;
    }

    @Override
    public String testConnection() {
        return delegator.testConnection();
    }

    @Override
    public String ping(Vector<Object> repositoryParams) {
        return delegator.ping(repositoryParams);
    }

    @Override
    public Vector<Object> getRunner(String name) {
        return delegator.getRunner(name);
    }

    @Override
    public Vector<Object> getAllRunners() {
        return delegator.getAllRunners();
    }

    @Override
    public String createRunner(Vector<Object> runnerParams) {
        return delegator.createRunner(runnerParams);
    }

    @Override
    public String updateRunner(String oldRunnerName, Vector<Object> runnerParams) {
        return delegator.updateRunner(oldRunnerName, runnerParams);
    }

    @Override
    public String removeRunner(String name) {
        return delegator.removeRunner(name);
    }

    @Override
    public Vector<Object> getRegisteredRepository(Vector<Object> repositoryParams) {
        return delegator.getRegisteredRepository(repositoryParams);
    }

    @Override
    public Vector<Object> registerRepository(Vector<Object> repositoryParams) {
        return delegator.registerRepository(repositoryParams);
    }

    @Override
    public String updateRepositoryRegistration(Vector<Object> repositoryParams) {
        return delegator.updateRepositoryRegistration(repositoryParams);
    }

    @Override
    public String removeRepository(String repositoryUid) {
        return delegator.removeRepository(repositoryUid);
    }

    @Override
    public Vector<Object> getAllProjects() {
        return delegator.getAllProjects();
    }

    @Override
    public Vector<Object> getAllSpecificationRepositories() {
        return delegator.getAllSpecificationRepositories();
    }

    @Override
    public Vector<Object> getSpecificationRepositoriesOfAssociatedProject(Vector<Object> repositoryParams) {
        return delegator.getSpecificationRepositoriesOfAssociatedProject(repositoryParams);
    }

    @Override
    public Vector<Object> getAllRepositoriesForSystemUnderTest(Vector<Object> systemUnderTestParams) {
        return delegator.getAllRepositoriesForSystemUnderTest(systemUnderTestParams);
    }

    @Override
    public Vector<Object> getSpecificationRepositoriesForSystemUnderTest(Vector<Object> systemUnderTestParams) {
        return delegator.getSpecificationRepositoriesForSystemUnderTest(systemUnderTestParams);
    }

    @Override
    public Vector<Object> getRequirementRepositoriesOfAssociatedProject(Vector<Object> repositoryParams) {
        return delegator.getRequirementRepositoriesOfAssociatedProject(repositoryParams);
    }

    @Override
    public Vector<Object> getSystemUnderTestsOfAssociatedProject(Vector<Object> repositoryParams) {
        return delegator.getSystemUnderTestsOfAssociatedProject(repositoryParams);
    }

    @Override
    public Vector<Object> getSystemUnderTestsOfProject(String projectName) {
        return delegator.getSystemUnderTestsOfProject(projectName);
    }

    @Override
    public String addSpecificationSystemUnderTest(Vector<Object> systemUnderTestParams, Vector<Object> specificationParams) {
        return delegator.addSpecificationSystemUnderTest(systemUnderTestParams, specificationParams);
    }

    @Override
    public String removeSpecificationSystemUnderTest(Vector<Object> systemUnderTestParams,
        Vector<Object> specificationParams) {
        return delegator.removeSpecificationSystemUnderTest(systemUnderTestParams, specificationParams);
    }

    @Override
    public String doesSpecificationHasReferences(Vector<Object> specificationParams) {
        return delegator.doesSpecificationHasReferences(specificationParams);
    }

    @Override
    public Vector<Object> getSpecificationReferences(Vector<Object> specificationParams) {
        return delegator.getSpecificationReferences(specificationParams);
    }

    @Override
    public String doesRequirementHasReferences(Vector<Object> requirementParams) {
        return delegator.doesRequirementHasReferences(requirementParams);
    }

    @Override
    public Vector<Object> getRequirementReferences(Vector<Object> requirementParams) {
        return delegator.getRequirementReferences(requirementParams);
    }

    @Override
    public Vector<Object> getReference(Vector<Object> referenceParams) {
        return delegator.getReference(referenceParams);
    }

    @Override
    public Vector<Object> getSystemUnderTest(Vector<Object> systemUnderTestParams, Vector<Object> repositoryParams) {
        return delegator.getSystemUnderTest(systemUnderTestParams, repositoryParams);
    }

    @Override
    public String createSystemUnderTest(Vector<Object> systemUnderTestParams, Vector<Object> repositoryParams) {
        return delegator.createSystemUnderTest(systemUnderTestParams, repositoryParams);
    }

    @Override
    public String updateSystemUnderTest(String oldSystemUnderTestName, Vector<Object> systemUnderTestParams,
        Vector<Object> repositoryParams) {
        return delegator.updateSystemUnderTest(oldSystemUnderTestName, systemUnderTestParams, repositoryParams);
    }

    @Override
    public String removeSystemUnderTest(Vector<Object> systemUnderTestParams, Vector<Object> repositoryParams) {
        return delegator.removeSystemUnderTest(systemUnderTestParams, repositoryParams);
    }

    @Override
    public String setSystemUnderTestAsDefault(Vector<Object> systemUnderTestParams, Vector<Object> repositoryParams) {
        return delegator.setSystemUnderTestAsDefault(systemUnderTestParams, repositoryParams);
    }

    @Override
    public String removeRequirement(Vector<Object> requirementParams) {
        return delegator.removeRequirement(requirementParams);
    }

    @Override
    public Vector<Object> getSpecification(Vector<Object> specificationParams) {
        return delegator.getSpecification(specificationParams);
    }

    @Override
    public Vector<Object> getSpecifications(Vector<Object> systemUnderTestParams, Vector<Object> repositoryParams) {
        return delegator.getSpecifications(systemUnderTestParams, repositoryParams);
    }

    @Override
    public Vector< ? > getListOfSpecificationLocations(String repositoryUID, String systemUnderTestName) {
        return delegator.getListOfSpecificationLocations(repositoryUID, systemUnderTestName);
    }

    @Override
    public Vector<Object> createSpecification(Vector<Object> specificationParams) {
        return delegator.createSpecification(specificationParams);
    }

    @Override
    public String updateSpecification(Vector<Object> oldSpecificationParams, Vector<Object> newSpecificationParams) {
        return delegator.updateSpecification(oldSpecificationParams, newSpecificationParams);
    }

    @Override
    public String removeSpecification(Vector<Object> specificationParams) {
        return delegator.removeSpecification(specificationParams);
    }

    @Override
    public String createReference(Vector<Object> referenceParams) {
        return delegator.createReference(referenceParams);
    }

    @Override
    public Vector<Object> updateReference(Vector<Object> oldReferenceParams, Vector<Object> newReferenceParams) {
        return delegator.updateReference(oldReferenceParams, newReferenceParams);
    }

    @Override
    public String removeReference(Vector<Object> referenceParams) {
        return delegator.removeReference(referenceParams);
    }

    @Override
    public Vector<Object> runSpecification(Vector<Object> systemUnderTestParams, Vector<Object> specificationParams,
        boolean implementedVersion, String locale) {
        return delegator.runSpecification(systemUnderTestParams, specificationParams, implementedVersion, locale);
    }

    @Override
    public Vector<Object> runReference(Vector<Object> referenceParams, String locale) {
        return delegator.runReference(referenceParams, locale);
    }

    @Override
    public Vector<Object> getRequirementSummary(Vector<Object> requirementParams) {
        return delegator.getRequirementSummary(requirementParams);
    }

    @Override
    public Vector<Object> getSpecificationHierarchy(Vector<Object> repositoryParams, Vector<Object> sutParams) {
        return delegator.getSpecificationHierarchy(repositoryParams, sutParams);
    }

    @Override
    public String getRenderedSpecification(String username, String password, Vector< ? > args) {
        return confluenceServiceDelegator.getRenderedSpecification(username, password, args);
    }

    @Override
    public Vector< ? > getSpecificationHierarchy(String username, String password, Vector< ? > args) {
        return confluenceServiceDelegator.getSpecificationHierarchy(username, password, args);
    }

    @Override
    public String setSpecificationAsImplemented(String username, String password, Vector< ? > args) {
        return confluenceServiceDelegator.setSpecificationAsImplemented(username, password, args);
    }

    @Override
    public String saveExecutionResult(String username, String password, Vector< ? > args) {
        return confluenceServiceDelegator.saveExecutionResult(username, password, args);
    }
}
