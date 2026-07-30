package com.app.learning.ui.auth;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.app.learning.data.api.Resource;
import com.app.learning.data.model.User;
import com.app.learning.data.repository.UserRepository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

public class AuthViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    private UserRepository userRepository;

    private AuthViewModel authViewModel;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        authViewModel = new AuthViewModel(userRepository);
    }

    @Test
    public void login_validationError_invalidEmail() {
        authViewModel.login("invalidemail", "password123");

        Resource<User> result = authViewModel.getAuthResult().getValue();
        assertNotNull(result);
        assertTrue(result.isError());
        assertEquals("Email không hợp lệ", result.error.getMessage());
    }

    @Test
    public void login_validationError_shortPassword() {
        authViewModel.login("test@example.com", "123");

        Resource<User> result = authViewModel.getAuthResult().getValue();
        assertNotNull(result);
        assertTrue(result.isError());
        assertEquals("Mật khẩu phải từ 6 ký tự trở lên", result.error.getMessage());
    }

    @Test
    public void login_success() {
        String email = "student@example.com";
        String password = "password123";

        User mockUser = new User();
        mockUser.setId("usr_100");
        mockUser.setEmail(email);
        mockUser.setFullName("Nguyen Van A");

        MutableLiveData<Resource<User>> liveData = new MutableLiveData<>();
        liveData.setValue(Resource.success(mockUser));

        when(userRepository.login(eq(email), eq(password))).thenReturn(liveData);

        authViewModel.login(email, password);

        Resource<User> result = authViewModel.getAuthResult().getValue();
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals("usr_100", result.data.getId());
        assertEquals("Nguyen Van A", result.data.getFullName());
    }

    @Test
    public void login_failure_wrongPassword() {
        String email = "student@example.com";
        String password = "wrongpassword";

        MutableLiveData<Resource<User>> liveData = new MutableLiveData<>();
        liveData.setValue(Resource.error(new com.app.learning.data.api.ApiError("Mật khẩu không chính xác")));

        when(userRepository.login(eq(email), eq(password))).thenReturn(liveData);

        authViewModel.login(email, password);

        Resource<User> result = authViewModel.getAuthResult().getValue();
        assertNotNull(result);
        assertTrue(result.isError());
        assertEquals("Mật khẩu không chính xác", result.error.getMessage());
    }

    @Test
    public void login_failure_networkError() {
        String email = "student@example.com";
        String password = "password123";

        MutableLiveData<Resource<User>> liveData = new MutableLiveData<>();
        liveData.setValue(Resource.error(new com.app.learning.data.api.ApiError("Không có kết nối Internet")));

        when(userRepository.login(eq(email), eq(password))).thenReturn(liveData);

        authViewModel.login(email, password);

        Resource<User> result = authViewModel.getAuthResult().getValue();
        assertNotNull(result);
        assertTrue(result.isError());
        assertEquals("Không có kết nối Internet", result.error.getMessage());
    }
}
