package info.novatec.testit.livingdoc.confluence.rest;

import info.novatec.testit.livingdoc.confluence.server.ConfluenceLivingDocServiceImpl;
import info.novatec.testit.livingdoc.confluence.utils.stylesheet.StyleSheetExtractor;
import info.novatec.testit.livingdoc.confluence.velocity.LivingDocConfluenceManager;
import info.novatec.testit.livingdoc.server.LivingDocPersistenceService;
import info.novatec.testit.livingdoc.server.LivingDocServerException;
import info.novatec.testit.livingdoc.server.domain.*;
import info.novatec.testit.livingdoc.server.rest.LivingDocRestHelper;
import info.novatec.testit.livingdoc.server.rest.requests.*;
import info.novatec.testit.livingdoc.server.rest.responses.*;
import info.novatec.testit.livingdoc.util.ClientUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Path("/command")
public class LivingDocRestServiceImpl implements LivingDocRestService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LivingDocRestServiceImpl.class);

    private LivingDocRestHelper livingDocRestHelper;
    private LivingDocPersistenceService livingDocServerService;
    private ObjectMapper objectMapper;
    private String username;
    private String password;


    public LivingDocRestServiceImpl(LivingDocPersistenceService livingDocPersistenceService,
                                    LivingDocConfluenceManager livingDocConfluenceManager,
                                    StyleSheetExtractor styleSheetExtractor) {

        this.livingDocServerService = livingDocPersistenceService;
        this.livingDocRestHelper = new ConfluenceLivingDocServiceImpl(livingDocConfluenceManager, styleSheetExtractor);

        this.objectMapper = new ObjectMapper();
        this.objectMapper.disable(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS);
        this.objectMapper.disable(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    public void setService(LivingDocPersistenceService livingDocServerService) {
        this.livingDocServerService = livingDocServerService;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String dispatchCommand(@HeaderParam("authorization") String authorization,
                                  @HeaderParam("method-name") final String methodName, final String body)
            throws LivingDocServerException {

        decodedAuthorization(authorization);

        return dispatchByMethodName(methodName, body);
    }

    private String dispatchByMethodName(final String methodName, final String body) {

        String result;

        try {
            Method method = LivingDocRestServiceImpl.class.getDeclaredMethod(methodName, String.class);
            result = (String) method.invoke(this, body);

        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            LOGGER.error(e.getMessage(), e);
            throw new IllegalArgumentException(
                    String.format("Method '%s()' not handled by this REST endpoint!", methodName), e);
        }

        return StringUtils.defaultString(result);
    }

    private void decodedAuthorization(final String authorization) throws LivingDocServerException {
        if (StringUtils.startsWith(authorization, "Basic")) {
            try {
                String[] result = ClientUtils.decodeFromBase64(authorization.substring("Basic".length()).trim(), ":");
                this.username = result[0];
                this.password = result[1];
            } catch (UnsupportedEncodingException uee) {
                LOGGER.error(uee.getMessage(), uee);
                throw new LivingDocServerException(uee);
            }
        } else {
            throw new LivingDocServerException("livingdoc.rest.authorization.not.supported", "Authorization not supported.");
        }
    }

    private String getRenderedSpecification(final String body) throws IOException {
        GetRenderedSpecificationRequest getRenderedSpecificationRequest = deserializeRequestBody(body, GetRenderedSpecificationRequest.class);
        String specification = livingDocRestHelper.getRenderedSpecification(username, password, new ArrayList<>(getRenderedSpecificationRequest.arguments));
        return serializeResponseBody(new GetRenderedSpecificationResponse(specification));
    }

    private String listDocumentsInHierarchy(final String body) throws IOException {
        ListDocumentsInHierarchyRequest listDocumentsInHierarchyRequest = deserializeRequestBody(body, ListDocumentsInHierarchyRequest.class);
        List<?> specifications = livingDocRestHelper.getSpecificationHierarchy(username, password, new ArrayList<>(listDocumentsInHierarchyRequest.arguments));
        return serializeResponseBody(new ListDocumentsInHierarchyResponse(specifications));
    }

    private String setSpecificationAsImplemented(final String body) throws IOException {
        SetSpecificationAsImplementedRequest setSpecificationAsImplementedRequest = deserializeRequestBody(body, SetSpecificationAsImplementedRequest.class);
        String message = livingDocRestHelper.setSpecificationAsImplemented(username, password, new ArrayList<>(setSpecificationAsImplementedRequest.arguments));
        return serializeResponseBody(new SetSpecificationAsImplementedResponse(message));
    }

    private String getRequirementSummary(final String body) throws IOException, LivingDocServerException {
        GetSummaryRequest getSummaryRequest = deserializeRequestBody(body, GetSummaryRequest.class);
        RequirementSummary requirementSummary = livingDocServerService.getRequirementSummary(getSummaryRequest.requirement);
        return serializeResponseBody(new GetSummaryResponse(requirementSummary));
    }

    private String getSpecificationHierarchy(final String body) throws IOException, LivingDocServerException {
        GetSpecificationHierarchyRequest getSpecificationHierarchyRequest = deserializeRequestBody(body, GetSpecificationHierarchyRequest.class);
        DocumentNode documentNode = livingDocServerService.getSpecificationHierarchy(getSpecificationHierarchyRequest.repository, getSpecificationHierarchyRequest.systemUnderTest);
        return serializeResponseBody(new GetSpecificationHierarchyResponse(documentNode));
    }

    private String runReference(final String body) throws IOException, LivingDocServerException {
        RunReferenceRequest runReferenceRequest = deserializeRequestBody(body, RunReferenceRequest.class);
        Reference runReference = livingDocServerService.runReference(runReferenceRequest.reference, runReferenceRequest.locale);
        return serializeResponseBody(new RunReferenceResponse(runReference));
    }

    private String runSpecification(final String body) throws IOException, LivingDocServerException {
        RunSpecificationRequest runSpecificationRequest = deserializeRequestBody(body, RunSpecificationRequest.class);
        Execution execution = livingDocServerService.runSpecification(runSpecificationRequest.systemUnderTest, runSpecificationRequest.specification, runSpecificationRequest.implementedVersion, runSpecificationRequest.locale);
        return serializeResponseBody(new RunSpecificationResponse(execution.marshallizeRest()));
    }

    private void removeReference(final String body) throws IOException, LivingDocServerException {
        RemoveReferenceRequest removeReferenceRequest = deserializeRequestBody(body, RemoveReferenceRequest.class);
        livingDocServerService.removeReference(removeReferenceRequest.reference);
    }

    private String updateReference(final String body) throws IOException, LivingDocServerException {
        UpdateReferenceRequest updateReferenceRequest = deserializeRequestBody(body, UpdateReferenceRequest.class);
        Reference updateReference = livingDocServerService.updateReference(updateReferenceRequest.oldReference, updateReferenceRequest.newReference);
        return serializeResponseBody(new UpdateReferenceResponse(updateReference));
    }

    private void createReference(final String body) throws IOException, LivingDocServerException {
        CreateReferenceRequest createReferenceRequest = deserializeRequestBody(body, CreateReferenceRequest.class);
        livingDocServerService.createReference(createReferenceRequest.reference);
    }

    private void removeSpecification(final String body) throws IOException, LivingDocServerException {
        RemoveSpecificationRequest removeSpecificationRequest = deserializeRequestBody(body, RemoveSpecificationRequest.class);
        livingDocServerService.removeSpecification(removeSpecificationRequest.specification);
    }

    private void updateSpecification(final String body) throws IOException, LivingDocServerException {
        UpdateSpecificationRequest updateSpecificationRequest = deserializeRequestBody(body, UpdateSpecificationRequest.class);
        livingDocServerService.updateSpecification(updateSpecificationRequest.oldSpecification, updateSpecificationRequest.newSpecification);
    }

    private String createSpecification(final String body) throws IOException, LivingDocServerException {
        CreateSpecificationRequest createSpecificationRequest = deserializeRequestBody(body, CreateSpecificationRequest.class);
        Specification createSpecification = livingDocServerService.createSpecification(createSpecificationRequest.specification);
        return serializeResponseBody(new CreateSpecificationResponse(createSpecification));
    }

    private String getSpecification(final String body) throws IOException, LivingDocServerException {
        GetSpecificationRequest getSpecificationRequest = deserializeRequestBody(body, GetSpecificationRequest.class);
        Specification specification = livingDocServerService.getSpecification(getSpecificationRequest.specification);
        return serializeResponseBody(new GetSpecificationResponse(specification));
    }

    private void removeRequirement(final String body) throws IOException, LivingDocServerException {
        RemoveRequirementRequest removeRequirementRequest = deserializeRequestBody(body, RemoveRequirementRequest.class);
        livingDocServerService.removeRequirement(removeRequirementRequest.requirement);
    }

    private void setSystemUnderTestAsDefault(final String body) throws IOException, LivingDocServerException {
        SetSystemUnderTestAsDefaultRequest setSystemUnderTestAsDefaultRequest = deserializeRequestBody(body, SetSystemUnderTestAsDefaultRequest.class);
        livingDocServerService.setSystemUnderTestAsDefault(setSystemUnderTestAsDefaultRequest.systemUnderTest, setSystemUnderTestAsDefaultRequest.repository);
    }

    private void removeSystemUnderTest(final String body) throws IOException, LivingDocServerException {
        RemoveSystemUnderTestRequest removeSystemUnderTestRequest = deserializeRequestBody(body, RemoveSystemUnderTestRequest.class);
        livingDocServerService.removeSystemUnderTest(removeSystemUnderTestRequest.systemUnderTest, removeSystemUnderTestRequest.repository);
    }

    private String testConnection(final String body) throws IOException {
        return serializeResponseBody(new TestConnectionResponse(Boolean.TRUE));
    }

    private void updateSystemUnderTest(final String body) throws IOException, LivingDocServerException {
        UpdateSystemUnderTestRequest updateSystemUnderTestRequest = deserializeRequestBody(body, UpdateSystemUnderTestRequest.class);
        livingDocServerService.updateSystemUnderTest(updateSystemUnderTestRequest.oldSystemUnderTestName, updateSystemUnderTestRequest.newSystemUnderTest, updateSystemUnderTestRequest.repository);
    }

    private String getSystemUnderTest(final String body) throws IOException, LivingDocServerException {
        GetSystemUnderTestRequest getSystemUnderTestRequest = deserializeRequestBody(body, GetSystemUnderTestRequest.class);
        SystemUnderTest systemUnderTest = livingDocServerService.getSystemUnderTest(getSystemUnderTestRequest.systemUnderTest, getSystemUnderTestRequest.repository);
        return serializeResponseBody(new GetSystemUnderTestResponse(systemUnderTest));
    }

    private void createSystemUnderTest(final String body) throws IOException, LivingDocServerException {
        CreateSystemUnderTestRequest createSystemUnderTestRequest = deserializeRequestBody(body, CreateSystemUnderTestRequest.class);
        livingDocServerService.createSystemUnderTest(createSystemUnderTestRequest.systemUnderTest, createSystemUnderTestRequest.repository);
    }

    private String getReference(final String body) throws IOException, LivingDocServerException {
        GetReferenceRequest getReferenceRequest = deserializeRequestBody(body, GetReferenceRequest.class);
        Reference reference = livingDocServerService.getReference(getReferenceRequest.reference);
        return serializeResponseBody(new GetReferenceResponse(reference));
    }

    private String getRequirementReferences(final String body) throws IOException, LivingDocServerException {
        GetRequirementReferencesRequest getRequirementReferencesRequest = deserializeRequestBody(body, GetRequirementReferencesRequest.class);
        Set<Reference> reqReferences = new HashSet<>(livingDocServerService.getRequirementReferences(getRequirementReferencesRequest.requirement));
        return serializeResponseBody(new GetRequirementReferencesResponse(reqReferences));
    }

    private String doesRequirementHasReferences(final String body) throws IOException, LivingDocServerException {
        HasRequirementReferencesRequest hasRequirementReferencesRequest = deserializeRequestBody(body, HasRequirementReferencesRequest.class);
        boolean hasRequirementReferences = livingDocServerService.doesRequirementHasReferences(hasRequirementReferencesRequest.requirement);
        return serializeResponseBody(new HasRequirementReferencesResponse(hasRequirementReferences));
    }

    private String getSpecificationReferences(final String body) throws IOException, LivingDocServerException {
        GetSpecificationReferencesRequest getSpecificationReferencesRequest = deserializeRequestBody(body, GetSpecificationReferencesRequest.class);
        Set<Reference> references = new HashSet<>(livingDocServerService.getSpecificationReferences(getSpecificationReferencesRequest.specification));
        return serializeResponseBody(new GetSpecificationReferencesResponse(references));
    }

    private String doesSpecificationHasReferences(final String body) throws IOException, LivingDocServerException {
        DoesSpecificationHasReferencesRequest doesSpecificationHasReferencesRequest = deserializeRequestBody(body, DoesSpecificationHasReferencesRequest.class);
        boolean doesSpecificationHasReferences = livingDocServerService.doesSpecificationHasReferences(doesSpecificationHasReferencesRequest.specification);
        return serializeResponseBody(new DoesSpecificationHasReferencesResponse(doesSpecificationHasReferences));
    }

    private void removeSpecificationSystemUnderTest(final String body) throws IOException, LivingDocServerException {
        RemoveSpecificationSystemUnderTestRequest removeSpecificationSystemUnderTestRequest = deserializeRequestBody(body, RemoveSpecificationSystemUnderTestRequest.class);
        livingDocServerService.removeSpecificationSystemUnderTest(removeSpecificationSystemUnderTestRequest.systemUnderTest, removeSpecificationSystemUnderTestRequest.specification);
    }

    private void addSpecificationSystemUnderTest(final String body) throws IOException, LivingDocServerException {
        AddSpecificationSystemUnderTestRequest addSpecificationSystemUnderTestRequest = deserializeRequestBody(body, AddSpecificationSystemUnderTestRequest.class);
        livingDocServerService.addSpecificationSystemUnderTest(addSpecificationSystemUnderTestRequest.systemUnderTest, addSpecificationSystemUnderTestRequest.specification);
    }

    private String getSystemUnderTestsOfProject(final String body) throws IOException, LivingDocServerException {
        GetSystemUnderTestsOfProjectRequest getSystemUnderTestsOfProjectRequest = deserializeRequestBody(body, GetSystemUnderTestsOfProjectRequest.class);
        Set<SystemUnderTest> systemUnderTestsOfProject = new HashSet<>(livingDocServerService.getSystemUnderTestsOfProject(getSystemUnderTestsOfProjectRequest.projectName));
        return serializeResponseBody(new GetSystemUnderTestsOfProjectResponse(systemUnderTestsOfProject));
    }

    private String getSystemUnderTestsOfAssociatedProject(final String body) throws IOException, LivingDocServerException {
        GetSystemUnderTestsOfAssociatedProjectRequest getSystemUnderTestsOfAssociatedProjectRequest = deserializeRequestBody(body, GetSystemUnderTestsOfAssociatedProjectRequest.class);
        Set<SystemUnderTest> systemUnderTestsOfAssociatedProject = new HashSet<>(livingDocServerService.getSystemUnderTestsOfAssociatedProject(getSystemUnderTestsOfAssociatedProjectRequest.repository.getUid()));
        return serializeResponseBody(new GetSystemUnderTestsOfAssociatedProjectResponse(systemUnderTestsOfAssociatedProject));
    }

    private String getRequirementRepositoriesOfAssociatedProject(final String body) throws IOException, LivingDocServerException {
        GetRequirementRepositoriesOfAssociatedProjectRequest getRequirementRepositoriesOfAssociatedProjectRequest = deserializeRequestBody(body, GetRequirementRepositoriesOfAssociatedProjectRequest.class);
        Set<Repository> requirementRepositoriesOfAssociatedProject = new HashSet<>(livingDocServerService.getRequirementRepositoriesOfAssociatedProject(getRequirementRepositoriesOfAssociatedProjectRequest.repository.getUid()));
        return serializeResponseBody(new GetRequirementRepositoriesOfAssociatedProjectResponse(requirementRepositoriesOfAssociatedProject));
    }

    private String getAllRepositoriesForSystemUnderTest(final String body) throws IOException, LivingDocServerException {
        GetAllRepositoriesForSystemUnderTestRequest getAllRepositoriesForSystemUnderTestRequest = deserializeRequestBody(body, GetAllRepositoriesForSystemUnderTestRequest.class);
        Set<Repository> allRepositories4Sut = new HashSet<>(livingDocServerService.getAllRepositoriesForSystemUnderTest(getAllRepositoriesForSystemUnderTestRequest.systemUnderTest));
        return serializeResponseBody(new GetAllRepositoriesForSystemUnderTestResponse(allRepositories4Sut));
    }

    private String getAllSpecificationRepositories(final String body) throws IOException, LivingDocServerException {
        Set<Repository> allSpecificationRepositories = new HashSet<>(livingDocServerService.getAllSpecificationRepositories());
        return serializeResponseBody(new GetAllSpecificationRepositoriesResponse(allSpecificationRepositories));
    }

    private String getSpecificationRepositoriesForSystemUnderTest(final String body) throws IOException, LivingDocServerException {
        GetSpecificationRepositoriesForSystemUnderTestRequest getSpecificationRepositoriesForSystemUnderTestRequest = deserializeRequestBody(body, GetSpecificationRepositoriesForSystemUnderTestRequest.class);
        Set<Repository> repositoriesListForSut = new HashSet<>(livingDocServerService.getSpecificationRepositoriesForSystemUnderTest(getSpecificationRepositoriesForSystemUnderTestRequest.systemUnderTest));
        return serializeResponseBody(new GetSpecificationRepositoriesForSystemUnderTestResponse(repositoriesListForSut));
    }

    private String getSpecificationRepositoriesOfAssociatedProject(final String body) throws IOException, LivingDocServerException {
        GetSpecificationRepositoriesOfAssociatedProjectRequest getSpecificationRepositoriesOfAssociatedProjectRequest = deserializeRequestBody(body, GetSpecificationRepositoriesOfAssociatedProjectRequest.class);
        Set<Repository> repositoriesList = new HashSet<>(livingDocServerService.getSpecificationRepositoriesOfAssociatedProject(getSpecificationRepositoriesOfAssociatedProjectRequest.repository.getUid()));
        return serializeResponseBody(new GetSpecificationRepositoriesOfAssociatedProjectResponse(repositoriesList));
    }

    private String getAllProjects(final String body) throws IOException, LivingDocServerException {
        Set<Project> projects = new HashSet<>(livingDocServerService.getAllProjects());
        return serializeResponseBody(new GetAllProjectsResponse(projects));
    }

    private void removeRepository(final String body) throws IOException, LivingDocServerException {
        RemoveRepositoryRequest removeRepositoryRequest = deserializeRequestBody(body, RemoveRepositoryRequest.class);
        livingDocServerService.removeRepository(removeRepositoryRequest.repositoryUid);
    }

    private void updateRepositoryRegistration(final String body) throws IOException, LivingDocServerException {
        UpdateRepositoryRegistrationRequest updateRepositoryRegistrationRequest = deserializeRequestBody(body, UpdateRepositoryRegistrationRequest.class);
        livingDocServerService.updateRepositoryRegistration(updateRepositoryRegistrationRequest.repository);
    }

    private String registerRepository(final String body) throws IOException, LivingDocServerException {
        RegisterRepositoryRequest registerRepositoryRequest = deserializeRequestBody(body, RegisterRepositoryRequest.class);
        Repository repository = livingDocServerService.registerRepository(registerRepositoryRequest.repository);
        return serializeResponseBody(new RegisterRepositoryResponse(repository));
    }

    private String getRegisteredRepository(final String body) throws IOException, LivingDocServerException {
        GetRegisteredRepositoryRequest getRegisteredRepositoryRequest = deserializeRequestBody(body, GetRegisteredRepositoryRequest.class);
        Repository registeredRepository = livingDocServerService.getRegisteredRepository(getRegisteredRepositoryRequest.repository);
        return serializeResponseBody(new GetRegisteredRepositoryResponse(registeredRepository));
    }

    private void removeRunner(final String body) throws IOException, LivingDocServerException {
        RemoveRunnerRequest removeRunnerRequest = deserializeRequestBody(body, RemoveRunnerRequest.class);
        livingDocServerService.removeRunner(removeRunnerRequest.name);
    }

    private void updateRunner(final String body) throws IOException, LivingDocServerException {
        UpdateRunnerRequest updateRunnerRequest = deserializeRequestBody(body, UpdateRunnerRequest.class);
        livingDocServerService.updateRunner(updateRunnerRequest.oldRunnerName, updateRunnerRequest.runner);
    }

    private void createRunner(final String body) throws IOException, LivingDocServerException {
        CreateRunnerRequest createRunnerRequest = deserializeRequestBody(body, CreateRunnerRequest.class);
        livingDocServerService.createRunner(createRunnerRequest.runner);
    }

    private String getAllRunners(final String body) throws LivingDocServerException, IOException {
        Set<Runner> runners = new HashSet<>(livingDocServerService.getAllRunners());
        return serializeResponseBody(new GetAllRunnersResponse(runners));
    }

    private String getRunner(final String body) throws IOException, LivingDocServerException {
        GetRunnerRequest getRunnerRequest = deserializeRequestBody(body, GetRunnerRequest.class);
        Runner runner = livingDocServerService.getRunner(getRunnerRequest.name);
        return serializeResponseBody(new GetRunnerResponse(runner));
    }

    private String ping(final String body) throws IOException {
        PingRequest pingRequest = deserializeRequestBody(body, PingRequest.class);
        boolean success = true;
        try {
            livingDocServerService.getRepository(pingRequest.repository.getUid(), pingRequest.repository.getMaxUsers());

        } catch (LivingDocServerException ldse) {
            LOGGER.warn(ldse.getMessage(), ldse);
            success = false;
        }
        return serializeResponseBody(new PingResponse(success));
    }

    private String serializeResponseBody(Object response) throws IOException {
        String result;
        try {
            result = objectMapper.writeValueAsString(response);

        } catch (JsonGenerationException | JsonMappingException jsone) {
            LOGGER.error(jsone.getMessage(), jsone);
            throw new IOException(jsone);
        }
        return result;
    }

    private <T> T deserializeRequestBody(String body, Class<T> clazz) throws IOException {
        return objectMapper.readValue(body, clazz);
    }
}