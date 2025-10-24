package org.acheron.authserver.service;

import com.google.protobuf.StringValue;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Metadata;
import io.grpc.stub.MetadataUtils;
import lombok.RequiredArgsConstructor;
import org.acheron.authserver.dto.UserCreationDto;
import org.acheron.user.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserGrpcClient {
    private final PasswordEncoder passwordEncoder;

    @Value("${spring.grpc.url}")
    private String GRPC_URL;


    public Long saveUser(UserCreationDto user, String accessToken) {
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress(GRPC_URL, 9091)
                .usePlaintext()
                .build();

        UserServiceGrpcGrpc.UserServiceGrpcBlockingStub stub =
                UserServiceGrpcGrpc.newBlockingStub(channel);

        Metadata headers = new Metadata();
        Metadata.Key<String> authKey = Metadata.Key.of("Authorization", Metadata.ASCII_STRING_MARSHALLER);
        headers.put(authKey, "Bearer " + accessToken);

        stub = stub.withInterceptors(MetadataUtils.newAttachHeadersInterceptor(headers));

        try {
            UserRequest request = UserRequest.newBuilder()
                    .setUsername(user.username())
                    .setEmail(user.email())
                    .setIsEmailVerified(user.isEmailVerified())
                    .setRole(user.role())
                    .setAuthMethod(user.authMethod())
                    .setPassword(StringValue.newBuilder().setValue(user.password()!=null? passwordEncoder.encode(user.password()) : "")
                            .build())
                    .setMfaEnabled(user.isMFAEnabled())
                    .setMfaSecret(StringValue.newBuilder().setValue(user.MFASecret()==null?"":user.MFASecret()).build())
                    .build();

            Id response = stub.saveUser(request);
            return response.getId();
        } finally {
            channel.shutdown();
        }
    }

    public UserDto findUserByUsername(String username, String accessToken) {
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress(GRPC_URL, 9091)
                .usePlaintext()
                .build();

        UserServiceGrpcGrpc.UserServiceGrpcBlockingStub stub =
                UserServiceGrpcGrpc.newBlockingStub(channel);

        Metadata headers = new Metadata();
        Metadata.Key<String> authKey = Metadata.Key.of("Authorization", Metadata.ASCII_STRING_MARSHALLER);
        headers.put(authKey, "Bearer " + accessToken);

        stub = stub.withInterceptors(MetadataUtils.newAttachHeadersInterceptor(headers));
        try {
            return stub.findUserByUsername(Username.newBuilder().setUsername(username).build());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            channel.shutdown();
        }
        return null; //TODO
    }

    public UserDto findUserByEmail(String email, String accessToken) {
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress(GRPC_URL, 9091)
                .usePlaintext()
                .build();

        UserServiceGrpcGrpc.UserServiceGrpcBlockingStub stub =
                UserServiceGrpcGrpc.newBlockingStub(channel);

        Metadata headers = new Metadata();
        Metadata.Key<String> authKey = Metadata.Key.of("Authorization", Metadata.ASCII_STRING_MARSHALLER);
        headers.put(authKey, "Bearer " + accessToken);

        stub = stub.withInterceptors(MetadataUtils.newAttachHeadersInterceptor(headers));
        try {
            return stub.findUserByEmail(Email.newBuilder().setEmail(email).build());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            channel.shutdown();
        }
        return null; //TODO
    }

    public boolean existsByEmail(String email, String accessToken) {
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress(GRPC_URL, 9091)
                .usePlaintext()
                .build();

        UserServiceGrpcGrpc.UserServiceGrpcBlockingStub stub =
                UserServiceGrpcGrpc.newBlockingStub(channel);

        Metadata headers = new Metadata();
        Metadata.Key<String> authKey = Metadata.Key.of("Authorization", Metadata.ASCII_STRING_MARSHALLER);
        headers.put(authKey, "Bearer " + accessToken);

        stub = stub.withInterceptors(MetadataUtils.newAttachHeadersInterceptor(headers));
        try {
            return stub.existsByEmail(Email.newBuilder().setEmail(email).build()).getIsExisting();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            channel.shutdown();
        }
        return false; //TODO
    }

}
