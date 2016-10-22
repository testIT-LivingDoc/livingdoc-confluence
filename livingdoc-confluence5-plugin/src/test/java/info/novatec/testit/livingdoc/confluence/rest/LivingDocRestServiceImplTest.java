package info.novatec.testit.livingdoc.confluence.rest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import info.novatec.testit.livingdoc.server.domain.Runner;
import info.novatec.testit.livingdoc.server.rpc.RpcClientService;


/**
 * @author Sebastian Letzel
 */
@RunWith(MockitoJUnitRunner.class)
public class LivingDocRestServiceImplTest {

    @Mock
    RpcClientService clientService;

    @InjectMocks
    LivingDocRestServiceImpl ldRestService;

    @Test
    public void dispatchByMethodName() throws Exception {
        Runner runner = Runner.newInstance("DocumentRunner");
        when(clientService.getRunner("DocumentRunner", null)).thenReturn(runner);
        String getRunner = ldRestService.dispatchByMethodName("getRunner", "{\"name\":\"DocumentRunner\"}");

        assertThat(getRunner, CoreMatchers.is("{\"runner\":{\"id\":null,\"version\":null,\"name\":\"DocumentRunner\",\"serverName\":null,\"serverPort\":null,\"secured\":false,\"classpaths\":[]}}"));
    }

}
