package LeoulGetnetMs.OrderService.Security.SecurityConfigGrpcTo;

import io.grpc.*;

public class BearerTokenInterceptor implements ClientInterceptor { //used when we use grpc

    private final String token;

    public BearerTokenInterceptor(String token) {
        this.token = token;
    }

    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(
            MethodDescriptor<ReqT, RespT> method,
            CallOptions callOptions,
            Channel next) {

        return new ForwardingClientCall.SimpleForwardingClientCall<ReqT, RespT>(
                next.newCall(method, callOptions)) {

            @Override
            public void start(Listener<RespT> responseListener, Metadata headers) {
                Metadata.Key<String> authKey =
                        Metadata.Key.of("Authorization", Metadata.ASCII_STRING_MARSHALLER);
                headers.put(authKey, "Bearer " + token);
                super.start(responseListener, headers);
            }
        };
    }
}