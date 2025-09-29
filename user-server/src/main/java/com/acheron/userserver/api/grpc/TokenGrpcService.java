package com.acheron.userserver.api.grpc;

import com.acheron.token.Email;
import com.acheron.token.Token;
import com.acheron.token.TokenServiceGrpcGrpc;
import com.acheron.userserver.service.AuthHandler;
import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import org.springframework.grpc.server.service.GrpcService;

@GrpcService
@RequiredArgsConstructor
public class TokenGrpcService extends TokenServiceGrpcGrpc.TokenServiceGrpcImplBase {

    private final AuthHandler authHandler;
    @Override
    public void resetPassword(Email request, StreamObserver<Empty> responseObserver) {
        try {
            authHandler.resetPassword(request.getEmail());
            responseObserver.onNext(Empty.newBuilder().build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void reset(Token request, StreamObserver<Token> responseObserver) {
        try {
            String newPassword = authHandler.reset(request.getToken());
            responseObserver.onNext(Token.newBuilder().setToken(newPassword).build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }
}
