package com.acheron.userserver.api.grpc;

import com.acheron.user.*;
import com.acheron.userserver.dto.UserCreateDto;
import com.acheron.userserver.entity.User;
import com.acheron.userserver.service.UserService;
import com.google.protobuf.Empty;
import com.google.protobuf.StringValue;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import org.springframework.grpc.server.service.GrpcService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@GrpcService
@RequiredArgsConstructor
public class UserGrpcService extends UserServiceGrpcGrpc.UserServiceGrpcImplBase {
    private final UserService userService;

    @Override
    public void findUserByUsername(Username request, StreamObserver<UserDto> responseObserver) {
        try {
            Optional<User> user = userService.findByUsername(request.getUsername());
            User user1 = user.orElseThrow(() -> new RuntimeException("User not found"));
            responseObserver.onNext(UserDto.newBuilder()
                    .setUsername(user1.getUsername())
                    .setPassword(StringValue.newBuilder().setValue(user1.getPassword()!=null?user1.getPassword():"").build())
                    .setEmail(user1.getEmail())
                    .setDisplayName(user1.getDisplayName())
                    .setImage(user1.getImage())
                    .setIsEmailVerified(user1.getIsEmailVerified())
                    .setRole(user1.getRole().name())
                    .setAuthMethod(user1.getAuthMethod().name())
                    .build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void existsByEmail(Email request, StreamObserver<Bool> responseObserver) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            boolean b = userService.existsByEmail(request.getEmail());
            responseObserver.onNext(Bool.newBuilder()
                    .setIsExisting(b)
                    .build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void findUserByEmail(Email request, StreamObserver<UserDto> responseObserver) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Optional<User> user = userService.findByEmail(request.getEmail());
            User user1 = user.orElseThrow(() -> new RuntimeException("User not found"));
            responseObserver.onNext(UserDto.newBuilder()
                    .setUsername(user1.getUsername())
                    .setPassword(StringValue.newBuilder().setValue(user1.getPassword()).build())
                    .setEmail(user1.getEmail())
                    .setDisplayName(user1.getDisplayName())
                    .setImage(user1.getImage())
                    .setIsEmailVerified(user1.getIsEmailVerified())
                    .setRole(user1.getRole().name())
                    .setAuthMethod(user1.getAuthMethod().name())
                    .build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void saveUser(UserRequest request, StreamObserver<Empty> responseObserver) {
        try {
            User user = User.builder()
                    .username(request.getUsername())
                    .password(request.getPassword().getValue())
                    .email(request.getEmail())
                    .displayName(request.getDisplayName())
                    .image(request.getImage())
                    .isEmailVerified(request.getIsEmailVerified())
                    .role(User.Role.valueOf(request.getRole()))
                    .authMethod(User.AuthMethod.valueOf(request.getAuthMethod()))
                    .build();
            if(user.getPassword().isEmpty()){
                user.setPassword(null);
            }
            userService.save(new UserCreateDto(user.getUsername(), user.getPassword(), user.getEmail(), user.getDisplayName(), user.getImage(),user.getIsEmailVerified(),user.getRole().name(),user.getAuthMethod().name()));
            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }
}
