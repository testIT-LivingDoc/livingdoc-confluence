package info.novatec.testit.livingdoc.confluence.rest;

import java.io.IOException;
import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.codehaus.jackson.map.ObjectMapper;

import info.novatec.testit.livingdoc.server.LivingDocServerException;
import info.novatec.testit.livingdoc.server.LivingDocServerService;
import info.novatec.testit.livingdoc.server.domain.Runner;
import info.novatec.testit.livingdoc.server.rest.requests.GetRunnerRequest;
import info.novatec.testit.livingdoc.server.rest.responses.GetRunnerResponse;
import info.novatec.testit.livingdoc.server.rpc.xmlrpc.XmlRpcMethodName;


@Path("/command")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class LivingDocRestServiceImpl implements LivingDocRestService {

    private ObjectMapper objectMapper = new ObjectMapper();

    private final LivingDocServerService ldServerService;

    public LivingDocRestServiceImpl(LivingDocServerService serverService) {
        ldServerService = serverService;
    }

    @POST
    public String dispatchCommand(@HeaderParam("method-name") String methodName, String body)
        throws IOException, LivingDocServerException {
        return dispatchByMethodName(methodName, body);
    }

    String dispatchByMethodName(String methodName, String body) throws IOException, LivingDocServerException {
        String result;
        XmlRpcMethodName xmlRpcMethodName = XmlRpcMethodName.valueOf(methodName);
        switch (xmlRpcMethodName) {
            case getRunner:
                GetRunnerRequest request = deserializeRequestBody(body, GetRunnerRequest.class);
                Runner runner = ldServerService.getRunner(request.name);
                result = serializeResponseBody(new GetRunnerResponse(runner));
                break;
            default:
                throw new IllegalArgumentException(
                    String.format("Method %s not handled by this REST endpoint!", xmlRpcMethodName));
        }

        return result;
    }

    private String serializeResponseBody(Object response) throws IOException {
        return objectMapper.writeValueAsString(response);
    }

    private <T> T deserializeRequestBody(String body, Class<T> clazz) throws IOException {
        return objectMapper.readValue(body, clazz);
    }
}
