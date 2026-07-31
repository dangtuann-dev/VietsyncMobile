package com.app.learning.data.repository;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.app.learning.data.api.ApiClient;
import com.app.learning.data.api.ApiError;
import com.app.learning.data.api.Resource;
import com.app.learning.data.api.TeacherApi;
import com.app.learning.data.api.CourseApi;
import com.app.learning.data.model.Course;
import com.app.learning.data.model.Lesson;
import com.example.vietsyncmobile.BuildConfig;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import retrofit2.Call;

public class TeacherCourseRepository extends BaseRepository {

    private final TeacherApi teacherApi;
    private final CourseApi courseApi;

    public TeacherCourseRepository(@NonNull Context context) {
        super();
        this.teacherApi = ApiClient.getInstance().createService(TeacherApi.class);
        this.courseApi = ApiClient.getInstance().createService(CourseApi.class);
    }

    public LiveData<Resource<Course>> createCourse(Course course) {
        MutableLiveData<Resource<List<Course>>> rawLiveData = new MutableLiveData<>();
        MediatorLiveData<Resource<Course>> resultLiveData = new MediatorLiveData<>();

        resultLiveData.setValue(Resource.loading());

        // We use "return=representation" to get the created object back from Supabase
        Call<List<Course>> call = teacherApi.createCourse(course, "return=representation");
        executeCall(call, rawLiveData);

        resultLiveData.addSource(rawLiveData, resource -> {
            if (resource.isLoading()) {
                resultLiveData.setValue(Resource.loading());
            } else if (resource.isSuccess() && resource.data != null && !resource.data.isEmpty()) {
                resultLiveData.setValue(Resource.success(resource.data.get(0)));
            } else if (resource.isError()) {
                resultLiveData.setValue(Resource.error(resource.error));
            } else {
                resultLiveData.setValue(Resource.error(new ApiError("500", "Failed to create course", null, null)));
            }
        });

        return resultLiveData;
    }

    public LiveData<Resource<String>> uploadThumbnail(byte[] imageBytes, String mimeType) {
        MutableLiveData<Resource<Map<String, String>>> rawLiveData = new MutableLiveData<>();
        MediatorLiveData<Resource<String>> resultLiveData = new MediatorLiveData<>();

        resultLiveData.setValue(Resource.loading());

        String filename = UUID.randomUUID().toString() + ".jpg";
        okhttp3.RequestBody body = okhttp3.RequestBody.create(
                imageBytes,
                okhttp3.MediaType.parse(mimeType)
        );

        Call<Map<String, String>> call = teacherApi.uploadThumbnail(filename, body, "true");
        executeCall(call, rawLiveData);

        resultLiveData.addSource(rawLiveData, resource -> {
            if (resource.isLoading()) {
                resultLiveData.setValue(Resource.loading());
            } else if (resource.isSuccess()) {
                String publicUrl = BuildConfig.SUPABASE_URL + "/storage/v1/object/public/course_thumbnails/" + filename;
                resultLiveData.setValue(Resource.success(publicUrl));
            } else if (resource.isError()) {
                resultLiveData.setValue(Resource.error(resource.error));
            }
        });

        return resultLiveData;
    }

    public LiveData<Resource<Lesson>> addLesson(Lesson lesson) {
        MutableLiveData<Resource<List<Lesson>>> rawLiveData = new MutableLiveData<>();
        MediatorLiveData<Resource<Lesson>> resultLiveData = new MediatorLiveData<>();

        resultLiveData.setValue(Resource.loading());

        Call<List<Lesson>> call = teacherApi.createLesson(lesson, "return=representation");
        executeCall(call, rawLiveData);

        resultLiveData.addSource(rawLiveData, resource -> {
            if (resource.isLoading()) {
                resultLiveData.setValue(Resource.loading());
            } else if (resource.isSuccess() && resource.data != null && !resource.data.isEmpty()) {
                resultLiveData.setValue(Resource.success(resource.data.get(0)));
            } else if (resource.isError()) {
                resultLiveData.setValue(Resource.error(resource.error));
            } else {
                resultLiveData.setValue(Resource.error(new ApiError("500", "Failed to add lesson", null, null)));
            }
        });

        return resultLiveData;
    }

    public LiveData<Resource<Void>> updateLessonOrder(String lessonId, int newOrder) {
        MutableLiveData<Resource<List<Lesson>>> rawLiveData = new MutableLiveData<>();
        MediatorLiveData<Resource<Void>> resultLiveData = new MediatorLiveData<>();

        resultLiveData.setValue(Resource.loading());

        Map<String, Object> updates = new java.util.HashMap<>();
        updates.put("order_index", newOrder); // Assuming column name is order_index

        Call<List<Lesson>> call = teacherApi.updateLesson("eq." + lessonId, updates, "return=minimal");
        executeCall(call, rawLiveData);

        resultLiveData.addSource(rawLiveData, resource -> {
            if (resource.isLoading()) {
                resultLiveData.setValue(Resource.loading());
            } else if (resource.isSuccess()) {
                resultLiveData.setValue(Resource.success(null));
            } else if (resource.isError()) {
                resultLiveData.setValue(Resource.error(resource.error));
            }
        });

        return resultLiveData;
    }

    public LiveData<Resource<Void>> deleteLesson(String lessonId) {
        MutableLiveData<Resource<Void>> resultLiveData = new MutableLiveData<>();
        resultLiveData.setValue(Resource.loading());

        Call<Void> call = teacherApi.deleteLesson("eq." + lessonId);
        executeCall(call, resultLiveData);

        return resultLiveData;
    }

    public LiveData<Resource<Void>> deleteCourse(String courseId) {
        MutableLiveData<Resource<Void>> resultLiveData = new MutableLiveData<>();
        resultLiveData.setValue(Resource.loading());

        Call<Void> call = teacherApi.deleteCourse("eq." + courseId);
        executeCall(call, resultLiveData);

        return resultLiveData;
    }

    public LiveData<Resource<Course>> updateCourse(String courseId, Map<String, Object> updates) {
        MutableLiveData<Resource<List<Course>>> rawLiveData = new MutableLiveData<>();
        MediatorLiveData<Resource<Course>> resultLiveData = new MediatorLiveData<>();
        resultLiveData.setValue(Resource.loading());

        Call<List<Course>> call = teacherApi.updateCourse("eq." + courseId, updates, "return=representation");
        executeCall(call, rawLiveData);

        resultLiveData.addSource(rawLiveData, resource -> {
            if (resource.isLoading()) {
                resultLiveData.setValue(Resource.loading());
            } else if (resource.isSuccess() && resource.data != null && !resource.data.isEmpty()) {
                resultLiveData.setValue(Resource.success(resource.data.get(0)));
            } else if (resource.isError()) {
                resultLiveData.setValue(Resource.error(resource.error));
            } else {
                resultLiveData.setValue(Resource.error(new ApiError("500", "Failed to update course", null, null)));
            }
        });

        return resultLiveData;
    }
    
    public LiveData<Resource<List<Lesson>>> getLessons(String courseId) {
        MutableLiveData<Resource<List<Lesson>>> resultLiveData = new MutableLiveData<>();
        resultLiveData.setValue(Resource.loading());
        
        Call<List<Lesson>> call = courseApi.getLessonsByCourseId("eq." + courseId, "order_index.asc");
        executeCall(call, resultLiveData);
        
        return resultLiveData;
    }

    public LiveData<Resource<List<Course>>> getCoursesForInstructor(String instructorId) {
        MutableLiveData<Resource<List<Course>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());
        Call<List<Course>> call = teacherApi.getCourses("eq." + instructorId, "*");
        executeCall(call, result);
        return result;
    }

    public LiveData<Resource<List<com.app.learning.data.model.Enrollment>>> getEnrollmentsForCourse(String courseId) {
        MutableLiveData<Resource<List<com.app.learning.data.model.Enrollment>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());
        Call<List<com.app.learning.data.model.Enrollment>> call = teacherApi.getEnrollments("eq." + courseId, "*,user:users(*)");
        executeCall(call, result);
        return result;
    }

    public LiveData<Resource<com.app.learning.data.model.Enrollment>> addStudentToCourse(String email, String courseId) {
        MediatorLiveData<Resource<com.app.learning.data.model.Enrollment>> result = new MediatorLiveData<>();
        result.setValue(Resource.loading());

        MutableLiveData<Resource<List<com.app.learning.data.model.User>>> userQueryLiveData = new MutableLiveData<>();
        Call<List<com.app.learning.data.model.User>> userCall = teacherApi.getUserByEmail("eq." + email);
        executeCall(userCall, userQueryLiveData);

        result.addSource(userQueryLiveData, userResource -> {
            if (userResource.isSuccess() && userResource.data != null) {
                if (userResource.data.isEmpty()) {
                    result.setValue(Resource.error(new ApiError("404", "Không tìm thấy học viên với email này", null, null)));
                } else {
                    com.app.learning.data.model.User student = userResource.data.get(0);
                    
                    Map<String, Object> body = new java.util.HashMap<>();
                    body.put("user_id", student.getId());
                    body.put("course_id", courseId);
                    body.put("progress_percent", 0);
                    
                    MutableLiveData<Resource<List<com.app.learning.data.model.Enrollment>>> addLiveData = new MutableLiveData<>();
                    Call<List<com.app.learning.data.model.Enrollment>> addCall = teacherApi.addEnrollment(body, "return=representation");
                    executeCall(addCall, addLiveData);
                    
                    result.addSource(addLiveData, addResource -> {
                        if (addResource.isSuccess() && addResource.data != null && !addResource.data.isEmpty()) {
                            com.app.learning.data.model.Enrollment enrollment = addResource.data.get(0);
                            enrollment.setUser(student);
                            result.setValue(Resource.success(enrollment));
                        } else if (addResource.isError()) {
                            result.setValue(Resource.error(addResource.error));
                        }
                    });
                }
            } else if (userResource.isError()) {
                result.setValue(Resource.error(userResource.error));
            }
        });

        return result;
    }

    public LiveData<Resource<Void>> updateStudentProgress(String userId, String courseId, int progress) {
        MutableLiveData<Resource<Void>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());
        Map<String, Object> updates = new java.util.HashMap<>();
        updates.put("progress_percent", progress);
        Call<Void> call = teacherApi.updateEnrollment("eq." + userId, "eq." + courseId, updates);
        executeCall(call, result);
        return result;
    }

    public LiveData<Resource<Void>> removeStudentFromCourse(String userId, String courseId) {
        MutableLiveData<Resource<Void>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());
        Call<Void> call = teacherApi.deleteEnrollment("eq." + userId, "eq." + courseId);
        executeCall(call, result);
        return result;
    }
}
