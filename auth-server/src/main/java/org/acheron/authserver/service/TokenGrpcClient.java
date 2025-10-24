package org.acheron.authserver.service;

import com.acheron.token.Email;
import com.acheron.token.Token;
import com.acheron.token.TokenServiceGrpcGrpc;
import com.google.protobuf.Empty;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Metadata;
import io.grpc.stub.MetadataUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenGrpcClient {

    private final OAuth2AuthorizedClientManager manager;

    @Value("${spring.grpc.url}")
    private String GRPC_URL;

    private String getAccessToken(){
        OAuth2AuthorizeRequest request1 = OAuth2AuthorizeRequest
                .withClientRegistrationId("auth-server-service")
                .principal("auth-server")
                .build();
        OAuth2AuthorizedClient authorize = manager.authorize(request1);
        return  authorize.getAccessToken().getTokenValue();
    }

    public void resetPassword(String email) {
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress(GRPC_URL, 9091)
                .usePlaintext()
                .build();
        TokenServiceGrpcGrpc.TokenServiceGrpcBlockingStub stub = TokenServiceGrpcGrpc.newBlockingStub(channel);
        Metadata headers = new Metadata();
        Metadata.Key<String> authKey = Metadata.Key.of("Authorization", Metadata.ASCII_STRING_MARSHALLER);
        headers.put(authKey, "Bearer " + getAccessToken());

        stub = stub.withInterceptors(MetadataUtils.newAttachHeadersInterceptor(headers));

        try {
            Email request = Email.newBuilder()
                    .setEmail(email)
                    .build();

            Empty response = stub.resetPassword(request);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            channel.shutdown();
        }
    }

    public String reset(String token) {
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress(GRPC_URL, 9091)
                .usePlaintext()
                .build();
        TokenServiceGrpcGrpc.TokenServiceGrpcBlockingStub stub = TokenServiceGrpcGrpc.newBlockingStub(channel);
        Metadata headers = new Metadata();
        Metadata.Key<String> authKey = Metadata.Key.of("Authorization", Metadata.ASCII_STRING_MARSHALLER);
        headers.put(authKey, "Bearer " + getAccessToken());

        stub = stub.withInterceptors(MetadataUtils.newAttachHeadersInterceptor(headers));

        try {
            Token request = Token.newBuilder()
                    .setToken(token)
                    .build();

            Token response = stub.reset(request);
            return response.getToken();
        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
            return null;
        } finally {
            channel.shutdown();
        }
    }
}
