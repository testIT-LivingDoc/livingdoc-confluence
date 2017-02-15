package info.novatec.testit.livingdoc.confluence.rest;

import info.novatec.testit.livingdoc.server.LivingDocServerException;
import info.novatec.testit.livingdoc.server.LivingDocServerService;
import info.novatec.testit.livingdoc.server.domain.*;
import info.novatec.testit.livingdoc.server.rest.requests.*;
import info.novatec.testit.livingdoc.server.rest.responses.*;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;


@Path("/command")
public class LivingDocRestService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LivingDocRestService.class);

    private LivingDocServerService livingDocServerService;
    private ObjectMapper objectMapper;

    /**
     * Constructor for IoC
     */
    public LivingDocRestService() {
        objectMapper = new ObjectMapper();
        objectMapper.disable(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS);
        objectMapper.disable(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    public void setLivingDocServerService(LivingDocServerService livingDocServerService) {
        this.livingDocServerService = livingDocServerService;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String dispatchCommand(@HeaderParam("method-name") final String methodName, final String body)
            throws LivingDocServerException {

        return dispatchByMethodName(methodName, body);
    }
    @JsonIgnore
    String dispatchByMethodName(final String methodName, final String body) throws LivingDocServerException {

        String result;

        try {
            Method method = LivingDocRestService.class.getDeclaredMethod(methodName, String.class);
            result = (String) method.invoke(this, body);

        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            LOGGER.error(e.getMessage(), e);
            throw new IllegalArgumentException(
                    String.format("Method %s not handled by this REST endpoint!", methodName), e);
        }

        return StringUtils.defaultString(result);
    }

    String getRequirementSummary(final String body) throws IOException, LivingDocServerException {
        GetSummaryRequest getSummaryRequest = deserializeRequestBody(body, GetSummaryRequest.class);
        RequirementSummary requirementSummary = livingDocServerService.getRequirementSummary(getSummaryRequest.requirement);
        return serializeResponseBody(new GetSummaryResponse(requirementSummary));
    }

    String getSpecificationHierarchy(final String body) throws IOException, LivingDocServerException {
        GetSpecificationHierarchyRequest getSpecificationHierarchyRequest = deserializeRequestBody(body, GetSpecificationHierarchyRequest.class);
        DocumentNode documentNode = livingDocServerService.getSpecificationHierarchy(getSpecificationHierarchyRequest.repository, getSpecificationHierarchyRequest.systemUnderTest);
        return serializeResponseBody(new GetSpecificationHierarchyResponse(documentNode));
    }

    String runReference(final String body) throws IOException, LivingDocServerException {
        RunReferenceRequest runReferenceRequest = deserializeRequestBody(body, RunReferenceRequest.class);
        Reference runReference = livingDocServerService.runReference(runReferenceRequest.reference, runReferenceRequest.locale);
        return serializeResponseBody(new RunReferenceResponse(runReference));
    }

    String runSpecification(final String body) throws IOException, LivingDocServerException {
        RunSpecificationRequest runSpecificationRequest = deserializeRequestBody(body, RunSpecificationRequest.class);
        Execution execution = livingDocServerService.runSpecification(runSpecificationRequest.systemUnderTest, runSpecificationRequest.specification, runSpecificationRequest.implementedVersion, runSpecificationRequest.locale);
        return serializeResponseBody(new RunSpecificationResponse(execution));
    }

    void removeReference(final String body) throws IOException, LivingDocServerException {
        RemoveReferenceRequest removeReferenceRequest = deserializeRequestBody(body, RemoveReferenceRequest.class);
        livingDocServerService.removeReference(removeReferenceRequest.reference);
    }

    String updateReference(final String body) throws IOException, LivingDocServerException {
        UpdateReferenceRequest updateReferenceRequest = deserializeRequestBody(body, UpdateReferenceRequest.class);
        Reference updateReference = livingDocServerService.updateReference(updateReferenceRequest.oldReference, updateReferenceRequest.newReference);
        return serializeResponseBody(new UpdateReferenceResponse(updateReference));
    }

    void createReference(final String body) throws IOException, LivingDocServerException {
        CreateReferenceRequest createReferenceRequest = deserializeRequestBody(body, CreateReferenceRequest.class);
        livingDocServerService.createReference(createReferenceRequest.reference);
    }

    void removeSpecification(final String body) throws IOException, LivingDocServerException {
        RemoveSpecificationRequest removeSpecificationRequest = deserializeRequestBody(body, RemoveSpecificationRequest.class);
        livingDocServerService.removeSpecification(removeSpecificationRequest.specification);
    }

    void updateSpecification(final String body) throws IOException, LivingDocServerException {
        UpdateSpecificationRequest updateSpecificationRequest = deserializeRequestBody(body, UpdateSpecificationRequest.class);
        livingDocServerService.updateSpecification(updateSpecificationRequest.oldSpecification, updateSpecificationRequest.newSpecification);
    }

    String createSpecification(final String body) throws IOException, LivingDocServerException {
        CreateSpecificationRequest createSpecificationRequest = deserializeRequestBody(body, CreateSpecificationRequest.class);
        Specification createSpecification = livingDocServerService.createSpecification(createSpecificationRequest.specification);
        return serializeResponseBody(new CreateSpecificationResponse(createSpecification));
    }

    String getSpecification(final String body) throws IOException, LivingDocServerException {
        GetSpecificationRequest getSpecificationRequest = deserializeRequestBody(body, GetSpecificationRequest.class);
        Specification specification = livingDocServerService.getSpecification(getSpecificationRequest.specification);
        return serializeResponseBody(new GetSpecificationResponse(specification));
    }

    void removeRequirement(final String body) throws IOException, LivingDocServerException {
        RemoveRequirementRequest removeRequirementRequest = deserializeRequestBody(body, RemoveRequirementRequest.class);
        livingDocServerService.removeRequirement(removeRequirementRequest.requirement);
    }

    void setSystemUnderTestAsDefault(final String body) throws IOException, LivingDocServerException {
        SetSystemUnderTestAsDefaultRequest setSystemUnderTestAsDefaultRequest = deserializeRequestBody(body, SetSystemUnderTestAsDefaultRequest.class);
        livingDocServerService.setSystemUnderTestAsDefault(setSystemUnderTestAsDefaultRequest.systemUnderTest, setSystemUnderTestAsDefaultRequest.repository);
    }

    void removeSystemUnderTest(final String body) throws IOException, LivingDocServerException {
        RemoveSystemUnderTestRequest removeSystemUnderTestRequest = deserializeRequestBody(body, RemoveSystemUnderTestRequest.class);
        livingDocServerService.removeSystemUnderTest(removeSystemUnderTestRequest.systemUnderTest, removeSystemUnderTestRequest.repository);
    }

    String testConnection(final String body) throws IOException {
        return serializeResponseBody(new TestConnectionResponse(Boolean.TRUE));
    }

    void updateSystemUnderTest(final String body) throws IOException, LivingDocServerException {
        UpdateSystemUnderTestRequest updateSystemUnderTestRequest = deserializeRequestBody(body, UpdateSystemUnderTestRequest.class);
        livingDocServerService.updateSystemUnderTest(updateSystemUnderTestRequest.oldSystemUnderTestName, updateSystemUnderTestRequest.newSystemUnderTest, updateSystemUnderTestRequest.repository);
    }

    String getSystemUnderTest(final String body) throws IOException, LivingDocServerException {
        GetSystemUnderTestRequest getSystemUnderTestRequest = deserializeRequestBody(body, GetSystemUnderTestRequest.class);
        SystemUnderTest systemUnderTest = livingDocServerService.getSystemUnderTest(getSystemUnderTestRequest.systemUnderTest, getSystemUnderTestRequest.repository);
        return serializeResponseBody(new GetSystemUnderTestResponse(systemUnderTest));
    }

    void createSystemUnderTest(final String body) throws IOException, LivingDocServerException {
        CreateSystemUnderTestRequest createSystemUnderTestRequest = deserializeRequestBody(body, CreateSystemUnderTestRequest.class);
        livingDocServerService.createSystemUnderTest(createSystemUnderTestRequest.systemUnderTest, createSystemUnderTestRequest.repository);
    }

    String getReference(final String body) throws IOException, LivingDocServerException {
        GetReferenceRequest getReferenceRequest = deserializeRequestBody(body, GetReferenceRequest.class);
        Reference reference = livingDocServerService.getReference(getReferenceRequest.reference);
        return serializeResponseBody(new GetReferenceResponse(reference));
    }

    String getRequirementReferences(final String body) throws IOException, LivingDocServerException {
        GetRequirementReferencesRequest getRequirementReferencesRequest = deserializeRequestBody(body, GetRequirementReferencesRequest.class);
        Set<Reference> reqReferences = new HashSet<>(livingDocServerService.getRequirementReferences(getRequirementReferencesRequest.requirement));
        return serializeResponseBody(new GetRequirementReferencesResponse(reqReferences));
    }

    String doesRequirementHasReferences(final String body) throws IOException, LivingDocServerException {
        HasRequirementReferencesRequest hasRequirementReferencesRequest = deserializeRequestBody(body, HasRequirementReferencesRequest.class);
        boolean hasRequirementReferences = livingDocServerService.doesRequirementHasReferences(hasRequirementReferencesRequest.requirement);
        return serializeResponseBody(new HasRequirementReferencesResponse(hasRequirementReferences));
    }

    String getSpecificationReferences(final String body) throws IOException, LivingDocServerException {
        GetSpecificationReferencesRequest getSpecificationReferencesRequest = deserializeRequestBody(body, GetSpecificationReferencesRequest.class);
        Set<Reference> references = new HashSet<>(livingDocServerService.getSpecificationReferences(getSpecificationReferencesRequest.specification));
        return serializeResponseBody(new GetSpecificationReferencesResponse(references));
    }

    String doesSpecificationHasReferences(final String body) throws IOException, LivingDocServerException {
        DoesSpecificationHasReferencesRequest doesSpecificationHasReferencesRequest = deserializeRequestBody(body, DoesSpecificationHasReferencesRequest.class);
        boolean doesSpecificationHasReferences = livingDocServerService.doesSpecificationHasReferences(doesSpecificationHasReferencesRequest.specification);
        return serializeResponseBody(new DoesSpecificationHasReferencesResponse(doesSpecificationHasReferences));
    }

    void removeSpecificationSystemUnderTest(final String body) throws IOException, LivingDocServerException {
        RemoveSpecificationSystemUnderTestRequest removeSpecificationSystemUnderTestRequest = deserializeRequestBody(body, RemoveSpecificationSystemUnderTestRequest.class);
        livingDocServerService.removeSpecificationSystemUnderTest(removeSpecificationSystemUnderTestRequest.systemUnderTest, removeSpecificationSystemUnderTestRequest.specification);
    }

    void addSpecificationSystemUnderTest(final String body) throws IOException, LivingDocServerException {
        AddSpecificationSystemUnderTestRequest addSpecificationSystemUnderTestRequest = deserializeRequestBody(body, AddSpecificationSystemUnderTestRequest.class);
        livingDocServerService.addSpecificationSystemUnderTest(addSpecificationSystemUnderTestRequest.systemUnderTest, addSpecificationSystemUnderTestRequest.specification);
    }

    String getSystemUnderTestsOfProject(final String body) throws IOException, LivingDocServerException {
        GetSystemUnderTestsOfProjectRequest getSystemUnderTestsOfProjectRequest = deserializeRequestBody(body, GetSystemUnderTestsOfProjectRequest.class);
        Set<SystemUnderTest> systemUnderTestsOfProject = new HashSet<>(livingDocServerService.getSystemUnderTestsOfProject(getSystemUnderTestsOfProjectRequest.projectName));
        return serializeResponseBody(new GetSystemUnderTestsOfProjectResponse(systemUnderTestsOfProject));
    }

    String getSystemUnderTestsOfAssociatedProject(final String body) throws IOException, LivingDocServerException {
        GetSystemUnderTestsOfAssociatedProjectRequest getSystemUnderTestsOfAssociatedProjectRequest = deserializeRequestBody(body, GetSystemUnderTestsOfAssociatedProjectRequest.class);
        Set<SystemUnderTest> systemUnderTestsOfAssociatedProject = new HashSet<>(livingDocServerService.getSystemUnderTestsOfAssociatedProject(getSystemUnderTestsOfAssociatedProjectRequest.repository.getUid()));
        return serializeResponseBody(new GetSystemUnderTestsOfAssociatedProjectResponse(systemUnderTestsOfAssociatedProject));
    }

    String getRequirementRepositoriesOfAssociatedProject(final String body) throws IOException, LivingDocServerException {
        GetRequirementRepositoriesOfAssociatedProjectRequest getRequirementRepositoriesOfAssociatedProjectRequest = deserializeRequestBody(body, GetRequirementRepositoriesOfAssociatedProjectRequest.class);
        Set<Repository> requirementRepositoriesOfAssociatedProject = new HashSet<>(livingDocServerService.getRequirementRepositoriesOfAssociatedProject(getRequirementRepositoriesOfAssociatedProjectRequest.repository.getUid()));
        return serializeResponseBody(new GetRequirementRepositoriesOfAssociatedProjectResponse(requirementRepositoriesOfAssociatedProject));
    }

    String getAllRepositoriesForSystemUnderTest(final String body) throws IOException, LivingDocServerException {
        GetAllRepositoriesForSystemUnderTestRequest getAllRepositoriesForSystemUnderTestRequest = deserializeRequestBody(body, GetAllRepositoriesForSystemUnderTestRequest.class);
        Set<Repository> allRepositories4Sut = new HashSet<>(livingDocServerService.getAllRepositoriesForSystemUnderTest(getAllRepositoriesForSystemUnderTestRequest.systemUnderTest));
        return serializeResponseBody(new GetAllRepositoriesForSystemUnderTestResponse(allRepositories4Sut));
    }

    String getAllSpecificationRepositories(final String body) throws IOException, LivingDocServerException {
        Set<Repository> allSpecificationRepositories = new HashSet<>(livingDocServerService.getAllSpecificationRepositories());
        return serializeResponseBody(new GetAllSpecificationRepositoriesResponse(allSpecificationRepositories));
    }

    String getSpecificationRepositoriesForSystemUnderTest(final String body) throws IOException, LivingDocServerException {
        GetSpecificationRepositoriesForSystemUnderTestRequest getSpecificationRepositoriesForSystemUnderTestRequest = deserializeRequestBody(body, GetSpecificationRepositoriesForSystemUnderTestRequest.class);
        Set<Repository> repositoriesListForSut = new HashSet<>(livingDocServerService.getSpecificationRepositoriesForSystemUnderTest(getSpecificationRepositoriesForSystemUnderTestRequest.systemUnderTest));
        return serializeResponseBody(new GetSpecificationRepositoriesForSystemUnderTestResponse(repositoriesListForSut));
    }

    String getSpecificationRepositoriesOfAssociatedProject(final String body) throws IOException, LivingDocServerException {
        GetSpecificationRepositoriesOfAssociatedProjectRequest getSpecificationRepositoriesOfAssociatedProjectRequest = deserializeRequestBody(body, GetSpecificationRepositoriesOfAssociatedProjectRequest.class);
        Set<Repository> repositoriesList = new HashSet<>(livingDocServerService.getSpecificationRepositoriesOfAssociatedProject(getSpecificationRepositoriesOfAssociatedProjectRequest.repository.getUid()));
        return serializeResponseBody(new GetSpecificationRepositoriesOfAssociatedProjectResponse(repositoriesList));
    }

    String getAllProjects(final String body) throws IOException, LivingDocServerException {
        Set<Project> projects = new HashSet<>(livingDocServerService.getAllProjects());
        return serializeResponseBody(new GetAllProjectsResponse(projects));
    }

    void removeRepository(final String body) throws IOException, LivingDocServerException {
        RemoveRepositoryRequest removeRepositoryRequest = deserializeRequestBody(body, RemoveRepositoryRequest.class);
        livingDocServerService.removeRepository(removeRepositoryRequest.repositoryUid);
    }

    void updateRepositoryRegistration(final String body) throws IOException, LivingDocServerException {
        UpdateRepositoryRegistrationRequest updateRepositoryRegistrationRequest = deserializeRequestBody(body, UpdateRepositoryRegistrationRequest.class);
        livingDocServerService.updateRepositoryRegistration(updateRepositoryRegistrationRequest.repository);
    }

    String registerRepository(final String body) throws IOException, LivingDocServerException {
        RegisterRepositoryRequest registerRepositoryRequest = deserializeRequestBody(body, RegisterRepositoryRequest.class);
        Repository repository = livingDocServerService.registerRepository(registerRepositoryRequest.repository);
        return serializeResponseBody(new RegisterRepositoryResponse(repository));
    }

    String getRegisteredRepository(final String body) throws IOException, LivingDocServerException {
        GetRegisteredRepositoryRequest getRegisteredRepositoryRequest = deserializeRequestBody(body, GetRegisteredRepositoryRequest.class);
        Repository registeredRepository = livingDocServerService.getRegisteredRepository(getRegisteredRepositoryRequest.repository);
        return serializeResponseBody(new GetRegisteredRepositoryResponse(registeredRepository));
    }

    void removeRunner(final String body) throws IOException, LivingDocServerException {
        RemoveRunnerRequest removeRunnerRequest = deserializeRequestBody(body, RemoveRunnerRequest.class);
        livingDocServerService.removeRunner(removeRunnerRequest.name);
    }

    void updateRunner(final String body) throws IOException, LivingDocServerException {
        UpdateRunnerRequest updateRunnerRequest = deserializeRequestBody(body, UpdateRunnerRequest.class);
        livingDocServerService.updateRunner(updateRunnerRequest.oldRunnerName, updateRunnerRequest.runner);
    }

    void createRunner(final String body) throws IOException, LivingDocServerException {
        CreateRunnerRequest createRunnerRequest = deserializeRequestBody(body, CreateRunnerRequest.class);
        livingDocServerService.createRunner(createRunnerRequest.runner);
    }

    String getAllRunners(final String body) throws LivingDocServerException, IOException {
        Set<Runner> runners = new HashSet<>(livingDocServerService.getAllRunners());
        return serializeResponseBody(new GetAllRunnersResponse(runners));
    }

    String getRunner(final String body) throws IOException, LivingDocServerException {
        GetRunnerRequest getRunnerRequest = deserializeRequestBody(body, GetRunnerRequest.class);
        Runner runner = livingDocServerService.getRunner(getRunnerRequest.name);
        return serializeResponseBody(new GetRunnerResponse(runner));
    }

    String ping(final String body) throws IOException {
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