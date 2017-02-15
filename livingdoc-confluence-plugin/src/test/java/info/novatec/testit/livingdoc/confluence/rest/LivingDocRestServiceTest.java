package info.novatec.testit.livingdoc.confluence.rest;

import info.novatec.testit.livingdoc.server.LivingDocServerService;
import info.novatec.testit.livingdoc.server.domain.*;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.hamcrest.CoreMatchers;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;


/**
 * @author Sebastian Letzel
 */
@RunWith(MockitoJUnitRunner.class)
public class LivingDocRestServiceTest {

    private static ObjectMapper objectMapper;
    private static Specification specification;
    private static Requirement requirement;
    private static Execution execution;
    private static List<Runner> runners;
    private static List<Project> projects;
    private static List<Repository> repositories;
    private static List<SystemUnderTest> systemUnderTests;
    private static List<Reference> references;
    private static String repositoryAsString;
    private static String runnerAsString;
    private static String systemUnderTestAsString;
    private static String repositoriesExpected;
    private static String specificationAsString;
    private static String requirementAsString;
    private static String referenceAsString;

    @Mock
    LivingDocServerService clientService;

    @InjectMocks
    LivingDocRestService ldRestService;


    @BeforeClass
    static public void setUp() throws Exception {

        objectMapper = new ObjectMapper();
        objectMapper.disable(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS);

        Runner runner = Runner.newInstance("DocumentRunner");
        runners = new ArrayList<>();
        runners.add(runner);
        runnerAsString = "{\"runner\":" + objectMapper.writeValueAsString(runner) + "}";

        Repository repository = Repository.newInstance("1");
        repository.setType(RepositoryType.newInstance("CONFLUENCE"));
        repository.setMaxUsers(1);
        repositories = new ArrayList<>();
        repositories.add(repository);
        repositoryAsString = "{\"repository\":" + objectMapper.writeValueAsString(repository) + "}";
        repositoriesExpected = "{\"specificationRepositoriesOfAssociatedProject\":" + objectMapper.writeValueAsString(repositories) + "}";

        Project project = Project.newInstance("LDProject");
        projects = new ArrayList<>();
        projects.add(project);

        SystemUnderTest systemUnderTest = SystemUnderTest.newInstance("sut");
        systemUnderTests = new ArrayList<>();
        systemUnderTests.add(systemUnderTest);
        systemUnderTestAsString = "{\"systemUnderTest\":" + objectMapper.writeValueAsString(systemUnderTest) + "}";

        specification = Specification.newInstance("DocumentSpecification");
        specification.setRepository(repository);
        specificationAsString = "{\"specification\":" + objectMapper.writeValueAsString(specification) + "}";

        Reference reference = new Reference();
        references = new ArrayList<>();
        references.add(reference);
        referenceAsString = "{\"reference\":" + objectMapper.writeValueAsString(references.get(0)) + "}";

        requirement = new Requirement();
        requirement.setRepository(repository);
        requirementAsString = "{\"requirement\":" + objectMapper.writeValueAsString(requirement) + "}";

        execution = new Execution();
    }

    @Test
    public void getRunner() throws Exception {

        when(clientService.getRunner("DocumentRunner")).thenReturn(runners.get(0));

        String result = ldRestService.dispatchByMethodName("getRunner", "{\"name\":\"DocumentRunner\"}");
        assertThat(result, CoreMatchers.is(runnerAsString));
    }

    @Test
    public void getAllRunners() throws Exception {

        when(clientService.getAllRunners()).thenReturn(runners);

        String expected = "{\"runners\":" + objectMapper.writeValueAsString(runners) + "}";
        String result = ldRestService.dispatchByMethodName("getAllRunners", null);
        assertThat(result, CoreMatchers.is(expected));
    }

    @Test
    public void createRunner() throws Exception {

        doNothing().when(clientService).createRunner(runners.get(0));

        String result = ldRestService.dispatchByMethodName("createRunner", runnerAsString);
        assertThat(result, CoreMatchers.is(""));
    }

    @Test
    public void updateRunner() throws Exception {

        doNothing().when(clientService).updateRunner(runners.get(0).getName(), runners.get(0));

        String body = "{\"oldRunnerName\":\"DocumentRunner\",\"runner\":" + objectMapper.writeValueAsString(runners.get(0)) + "}";
        String result = ldRestService.dispatchByMethodName("updateRunner", body);
        assertThat(result, CoreMatchers.is(""));
    }

    @Test
    public void removeRunner() throws Exception {

        doNothing().when(clientService).removeRunner(runners.get(0).getName());

        String result = ldRestService.dispatchByMethodName("removeRunner", "{\"name\":\"DocumentRunner\"}");
        assertThat(result, CoreMatchers.is(""));
    }

    @Test
    public void getRegisteredRepository() throws Exception {

        when(clientService.getRegisteredRepository(repositories.get(0))).thenReturn(repositories.get(0));

        String result = ldRestService.dispatchByMethodName("getRegisteredRepository", repositoryAsString);
        assertThat(result, CoreMatchers.is(repositoryAsString));
    }

    @Test
    public void registerRepository() throws Exception {

        when(clientService.registerRepository(repositories.get(0))).thenReturn(repositories.get(0));

        String result = ldRestService.dispatchByMethodName("registerRepository", repositoryAsString);
        assertThat(result, CoreMatchers.is(repositoryAsString));
    }

    @Test
    public void updateRepositoryRegistration() throws Exception {

        doNothing().when(clientService).updateRepositoryRegistration(repositories.get(0));

        String result = ldRestService.dispatchByMethodName("updateRepositoryRegistration", repositoryAsString);
        assertThat(result, CoreMatchers.is(""));
    }

    @Test
    public void removeRepository() throws Exception {

        doNothing().when(clientService).removeRepository(repositories.get(0).getUid());

        String result = ldRestService.dispatchByMethodName("removeRepository", "{\"repositoryUid\":\"1\"}");
        assertThat(result, CoreMatchers.is(""));
    }

    @Test
    public void getAllProjects() throws Exception {

        when(clientService.getAllProjects()).thenReturn(projects);

        String expected = "{\"allProjects\":" + objectMapper.writeValueAsString(projects) + "}";
        String result = ldRestService.dispatchByMethodName("getAllProjects", null);
        assertThat(result, CoreMatchers.is(expected));
    }

    @Test
    public void getSpecificationRepositoriesOfAssociatedProject() throws Exception {

        when(clientService.getSpecificationRepositoriesOfAssociatedProject(repositories.get(0).getUid())).thenReturn(repositories);

        String result = ldRestService.dispatchByMethodName("getSpecificationRepositoriesOfAssociatedProject", repositoryAsString);
        assertThat(result, CoreMatchers.is(repositoriesExpected));
    }

    @Test
    public void getSpecificationRepositoriesForSystemUnderTest() throws Exception {

        when(clientService.getSpecificationRepositoriesForSystemUnderTest(systemUnderTests.get(0))).thenReturn(repositories);

        String result = ldRestService.dispatchByMethodName("getSpecificationRepositoriesForSystemUnderTest", systemUnderTestAsString);
        assertThat(result, CoreMatchers.is(repositoriesExpected));
    }

    @Test
    public void getAllSpecificationRepositories() throws Exception {

        when(clientService.getAllSpecificationRepositories()).thenReturn(repositories);

        String expected = "{\"allSpecificationRepositories\":" + objectMapper.writeValueAsString(repositories) + "}";
        String result = ldRestService.dispatchByMethodName("getAllSpecificationRepositories", null);
        assertThat(result, CoreMatchers.is(expected));
    }

    @Test
    public void getAllRepositoriesForSystemUnderTest() throws Exception {

        when(clientService.getAllRepositoriesForSystemUnderTest(systemUnderTests.get(0))).thenReturn(repositories);

        String expected = "{\"allRepositoriesForSystemUnderTest\":" + objectMapper.writeValueAsString(repositories) + "}";
        String result = ldRestService.dispatchByMethodName("getAllRepositoriesForSystemUnderTest", systemUnderTestAsString);
        assertThat(result, CoreMatchers.is(expected));
    }

    @Test
    public void getRequirementRepositoriesOfAssociatedProject() throws Exception {

        when(clientService.getRequirementRepositoriesOfAssociatedProject(repositories.get(0).getUid())).thenReturn(repositories);

        String expected = "{\"requirementRepositoriesOfAssociatedProject\":" + objectMapper.writeValueAsString(repositories) + "}";
        String result = ldRestService.dispatchByMethodName("getRequirementRepositoriesOfAssociatedProject", repositoryAsString);
        assertThat(result, CoreMatchers.is(expected));
    }

    @Test
    public void getSystemUnderTestsOfAssociatedProject() throws Exception {

        when(clientService.getSystemUnderTestsOfAssociatedProject(repositories.get(0).getUid())).thenReturn(systemUnderTests);

        String expected = "{\"systemUnderTestsOfAssociatedProject\":" + objectMapper.writeValueAsString(systemUnderTests) + "}";
        String result = ldRestService.dispatchByMethodName("getSystemUnderTestsOfAssociatedProject", repositoryAsString);
        assertThat(result, CoreMatchers.is(expected));
    }

    @Test
    public void getSystemUnderTestsOfProject() throws Exception {

        when(clientService.getSystemUnderTestsOfProject(projects.get(0).getName())).thenReturn(systemUnderTests);

        String expected = "{\"systemUnderTestsOfProject\":" + objectMapper.writeValueAsString(systemUnderTests) + "}";
        String result = ldRestService.dispatchByMethodName("getSystemUnderTestsOfProject", "{\"projectName\":\"LDProject\"}");
        assertThat(result, CoreMatchers.is(expected));
    }

    @Test
    public void addSpecificationSystemUnderTest() throws Exception {

        doNothing().when(clientService).addSpecificationSystemUnderTest(systemUnderTests.get(0), specification);

        String body = "{\"systemUnderTest\": " + objectMapper.writeValueAsString(systemUnderTests.get(0)) + ", \"specification\":" + objectMapper.writeValueAsString(specification) + "}";
        String result = ldRestService.dispatchByMethodName("addSpecificationSystemUnderTest", body);
        assertThat(result, CoreMatchers.is(""));
    }

    @Test
    public void removeSpecificationSystemUnderTest() throws Exception {

        doNothing().when(clientService).removeSpecificationSystemUnderTest(systemUnderTests.get(0), specification);

        String body = "{\"systemUnderTest\": " + objectMapper.writeValueAsString(systemUnderTests.get(0)) + ", \"specification\":" + objectMapper.writeValueAsString(specification) + "}";
        String result = ldRestService.dispatchByMethodName("removeSpecificationSystemUnderTest", body);
        assertThat(result, CoreMatchers.is(""));
    }

    @Test
    public void doesSpecificationHasReferences() throws Exception {

        when(clientService.doesSpecificationHasReferences(specification)).thenReturn(Boolean.TRUE);

        String result = ldRestService.dispatchByMethodName("doesSpecificationHasReferences", specificationAsString);
        assertThat(result, CoreMatchers.is("{\"hasReferences\":true}"));
    }

    @Test
    public void getSpecificationReferences() throws Exception {

        when(clientService.getSpecificationReferences(specification)).thenReturn(references);

        String result = ldRestService.dispatchByMethodName("getSpecificationReferences", specificationAsString);
        assertThat(result, CoreMatchers.is("{\"references\":" + objectMapper.writeValueAsString(references) + "}"));
    }

    @Test
    public void doesRequirementHasReferences() throws Exception {

        when(clientService.doesRequirementHasReferences(requirement)).thenReturn(Boolean.TRUE);

        String result = ldRestService.dispatchByMethodName("doesRequirementHasReferences", requirementAsString);
        assertThat(result, CoreMatchers.is("{\"value\":true}"));
    }

    @Test
    public void getRequirementReferences() throws Exception {

        when(clientService.getRequirementReferences(requirement)).thenReturn(references);

        String result = ldRestService.dispatchByMethodName("getRequirementReferences", requirementAsString);
        assertThat(result, CoreMatchers.is("{\"references\":" + objectMapper.writeValueAsString(references) + "}"));
    }

    @Test
    public void getReference() throws Exception {

        when(clientService.getReference(references.get(0))).thenReturn(references.get(0));

        String result = ldRestService.dispatchByMethodName("getReference", referenceAsString);
        assertThat(result, CoreMatchers.is(referenceAsString));
    }

    @Test
    public void createSystemUnderTest() throws Exception {

        doNothing().when(clientService).createSystemUnderTest(systemUnderTests.get(0), repositories.get(0));

        String body = "{\"systemUnderTest\": " + objectMapper.writeValueAsString(systemUnderTests.get(0)) + ", \"repository\":" + objectMapper.writeValueAsString(repositories.get(0)) + "}";
        String result = ldRestService.dispatchByMethodName("createSystemUnderTest", body);
        assertThat(result, CoreMatchers.is(""));
    }

    @Test
    public void getSystemUnderTest() throws Exception {

        when(clientService.getSystemUnderTest(systemUnderTests.get(0), repositories.get(0))).thenReturn(systemUnderTests.get(0));

        String body = "{\"systemUnderTest\": " + objectMapper.writeValueAsString(systemUnderTests.get(0)) + ", \"repository\":" + objectMapper.writeValueAsString(repositories.get(0)) + "}";
        String result = ldRestService.dispatchByMethodName("getSystemUnderTest", body);
        assertThat(result, CoreMatchers.is(systemUnderTestAsString));
    }

    @Test
    public void updateSystemUnderTest() throws Exception {

        doNothing().when(clientService).updateSystemUnderTest(systemUnderTests.get(0).getName(), systemUnderTests.get(0), repositories.get(0));

        String body = "{\"oldSystemUnderTestName\":\"sut\""
                + ", \"systemUnderTest\": " + objectMapper.writeValueAsString(systemUnderTests.get(0))
                + ", \"repository\":" + objectMapper.writeValueAsString(repositories.get(0)) + "}";
        String result = ldRestService.dispatchByMethodName("updateSystemUnderTest", body);
        assertThat(result, CoreMatchers.is(""));
    }

    @Test
    public void removeSystemUnderTest() throws Exception {

        doNothing().when(clientService).removeSystemUnderTest(systemUnderTests.get(0), repositories.get(0));

        String body = "{\"systemUnderTest\": " + objectMapper.writeValueAsString(systemUnderTests.get(0))
                + ", \"repository\":" + objectMapper.writeValueAsString(repositories.get(0)) + "}";
        String result = ldRestService.dispatchByMethodName("removeSystemUnderTest", body);
        assertThat(result, CoreMatchers.is(""));
    }

    @Test
    public void setSystemUnderTestAsDefault() throws Exception {

        doNothing().when(clientService).setSystemUnderTestAsDefault(systemUnderTests.get(0), repositories.get(0));

        String body = "{\"systemUnderTest\": " + objectMapper.writeValueAsString(systemUnderTests.get(0))
                + ", \"repository\":" + objectMapper.writeValueAsString(repositories.get(0)) + "}";
        String result = ldRestService.dispatchByMethodName("setSystemUnderTestAsDefault", body);
        assertThat(result, CoreMatchers.is(""));
    }

    @Test
    public void removeRequirement() throws Exception {

        doNothing().when(clientService).removeRequirement(requirement);

        String result = ldRestService.dispatchByMethodName("removeRequirement", requirementAsString);
        assertThat(result, CoreMatchers.is(""));
    }

    @Test
    public void getSpecification() throws Exception {

        when(clientService.getSpecification(specification)).thenReturn(specification);

        String result = ldRestService.dispatchByMethodName("getSpecification", specificationAsString);
        assertThat(result, CoreMatchers.is(specificationAsString));
    }

    @Test
    public void createSpecification() throws Exception {

        when(clientService.createSpecification(specification)).thenReturn(specification);

        String result = ldRestService.dispatchByMethodName("createSpecification", specificationAsString);
        assertThat(result, CoreMatchers.is(specificationAsString));
    }

    @Test
    public void updateSpecification() throws Exception {

        doNothing().when(clientService).updateSpecification(specification, specification);

        String result = ldRestService.dispatchByMethodName("updateSpecification", specificationAsString);
        assertThat(result, CoreMatchers.is(""));
    }

    @Test
    public void removeSpecification() throws Exception {

        doNothing().when(clientService).removeSpecification(specification);

        String result = ldRestService.dispatchByMethodName("removeSpecification", specificationAsString);
        assertThat(result, CoreMatchers.is(""));
    }

    @Test
    public void createReference() throws Exception {

        doNothing().when(clientService).createReference(references.get(0));

        String result = ldRestService.dispatchByMethodName("createReference", referenceAsString);
        assertThat(result, CoreMatchers.is(""));
    }

    @Test
    public void updateReference() throws Exception {

        when(clientService.updateReference(references.get(0), references.get(0))).thenReturn(references.get(0));

        String body =
                "{\"oldReference\": " + objectMapper.writeValueAsString(references.get(0)) + "," +
                        "\"newReference\":" + objectMapper.writeValueAsString(references.get(0)) + "}";
        String result = ldRestService.dispatchByMethodName("updateReference", body);
        assertThat(result, CoreMatchers.is(referenceAsString));
    }

    @Test
    public void removeReference() throws Exception {

        doNothing().when(clientService).removeReference(references.get(0));

        String result = ldRestService.dispatchByMethodName("removeReference", referenceAsString);
        assertThat(result, CoreMatchers.is(""));
    }

    @Test
    public void runSpecification() throws Exception {

        when(clientService.runSpecification(systemUnderTests.get(0), specification, true, "en")).thenReturn(execution);

        String expected = "{\"execution\":" + objectMapper.writeValueAsString(execution) + "}";
        String body = "{\"systemUnderTest\":" + objectMapper.writeValueAsString(systemUnderTests.get(0)) +
                ",\"specification\":" + objectMapper.writeValueAsString(specification) +
                ",\"implementedVersion\":true" +
                ",\"locale\":\"en\"}";
        String result = ldRestService.dispatchByMethodName("runSpecification", body);
        assertThat(result, CoreMatchers.is(expected));
    }

    @Test
    public void runReference() throws Exception {

        when(clientService.runReference(references.get(0), "en")).thenReturn(references.get(0));

        String body = "{\"reference\":" + objectMapper.writeValueAsString(references.get(0)) +
                ",\"locale\":\"en\"}";
        String result = ldRestService.dispatchByMethodName("runReference", body);
        assertThat(result, CoreMatchers.is(referenceAsString));
    }

    @Test
    public void getSpecificationHierarchy() throws Exception {

        DocumentNode documentNode = new DocumentNode("DocumentNode");
        when(clientService.getSpecificationHierarchy(repositories.get(0), systemUnderTests.get(0))).thenReturn(documentNode);

        String body = "{\"systemUnderTest\": " + objectMapper.writeValueAsString(systemUnderTests.get(0))
                + ", \"repository\":" + objectMapper.writeValueAsString(repositories.get(0)) + "}";
        String result = ldRestService.dispatchByMethodName("getSpecificationHierarchy", body);
        assertThat(result, CoreMatchers.is("{\"documentNode\":" + objectMapper.writeValueAsString(documentNode) + "}"));
    }

    @Test
    public void getRequirementSummary() throws Exception {

        RequirementSummary requirementSummary = new RequirementSummary();
        when(clientService.getRequirementSummary(requirement)).thenReturn(requirementSummary);

        String result = ldRestService.dispatchByMethodName("getRequirementSummary", requirementAsString);
        assertThat(result, CoreMatchers.is("{\"requirementSummary\":" + objectMapper.writeValueAsString(requirementSummary) + "}"));
    }

    @Test
    public void ping() throws Exception {

        when(clientService.getRepository(repositories.get(0).getUid(), repositories.get(0).getMaxUsers())).thenReturn(repositories.get(0));

        String result = ldRestService.dispatchByMethodName("ping", repositoryAsString);
        assertThat(result, CoreMatchers.is("{\"success\":true}"));
    }

    @Test
    public void testConnection() throws Exception {
        String result = ldRestService.dispatchByMethodName("testConnection", null);
        assertThat(result, CoreMatchers.is("{\"success\":true}"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void dispatchByMethodNameException() throws Exception {

        ldRestService.dispatchByMethodName("getMethodNotHandled", null);
    }

}
