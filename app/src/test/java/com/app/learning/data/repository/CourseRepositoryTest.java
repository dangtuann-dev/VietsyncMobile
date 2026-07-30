package com.app.learning.data.repository;

import com.app.learning.data.api.CourseApi;
import com.app.learning.data.model.Course;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.*;

public class CourseRepositoryTest {

    private MockWebServer mockWebServer;
    private CourseApi courseApi;

    @Before
    public void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mockWebServer.url("/"))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        courseApi = retrofit.create(CourseApi.class);
    }

    @After
    public void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    public void getCourses_success_returnsCourseList() throws Exception {
        String mockJsonResponse = "[\n" +
                "  {\n" +
                "    \"id\": \"c101\",\n" +
                "    \"title\": \"Lập Trình Android Java\",\n" +
                "    \"instructor_name\": \"Nguyen Van A\",\n" +
                "    \"price\": 500000\n" +
                "  },\n" +
                "  {\n" +
                "    \"id\": \"c102\",\n" +
                "    \"title\": \"Supabase REST Backend\",\n" +
                "    \"instructor_name\": \"Tran Van B\",\n" +
                "    \"price\": 0\n" +
                "  }\n" +
                "]";

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(mockJsonResponse));

        Response<List<Course>> response = courseApi.getCourses("*", "created_at.desc").execute();

        assertTrue(response.isSuccessful());
        assertNotNull(response.body());
        assertEquals(2, response.body().size());
        assertEquals("c101", response.body().get(0).getId());
        assertEquals("Lập Trình Android Java", response.body().get(0).getTitle());
    }

    @Test
    public void getCourses_error_unauthorized() throws Exception {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(401)
                .setBody("{\"message\":\"JWT token expired\"}"));

        Response<List<Course>> response = courseApi.getCourses("*", "created_at.desc").execute();

        assertFalse(response.isSuccessful());
        assertEquals(401, response.code());
    }

    @Test
    public void getCourses_error_serverError() throws Exception {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(500)
                .setBody("{\"message\":\"Internal Database Error\"}"));

        Response<List<Course>> response = courseApi.getCourses("*", "created_at.desc").execute();

        assertFalse(response.isSuccessful());
        assertEquals(500, response.code());
    }
}
