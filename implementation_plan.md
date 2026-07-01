# Implementation Plan - Android Learning App Search Feature

This plan details the implementation of a comprehensive search functionality for the learning app using Room database for local search history, Retrofit to call Supabase REST APIs (using Postgrest syntax), and material design layouts for search and filtering.

## User Review Required

> [!IMPORTANT]
> - **Room Dependency Installation**: We will add Room database dependency (`androidx.room:room-runtime:2.6.1` and its annotation processor) to `gradle/libs.versions.toml` and `app/build.gradle.kts`.
> - **Supabase Query Pattern**: We will query Supabase via Retrofit using the `@QueryMap` annotation, allowing dynamic construction of filter fields such as `title=ilike.*{query}*`, `level=eq.{level}`, `price=and(gte.{min},lte.{max})`, and `duration=and(gte.{min},lte.{max})`.
> - **Instructor Details**: We will add a nested `instructor` field of type `User` inside the `Course` model to map the relation `instructor:users(full_name)` returned by Supabase.

---

## Proposed Changes

### Build Configurations

#### [MODIFY] [libs.versions.toml](file:///d:/Vietsync/VietsyncMobile/gradle/libs.versions.toml)
- Add `room` version.
- Add `androidx-room-runtime` and `androidx-room-compiler` libraries.

#### [MODIFY] [build.gradle.kts](file:///d:/Vietsync/VietsyncMobile/app/build.gradle.kts)
- Include Room implementation dependency.
- Include Room compiler dependency as `annotationProcessor`.

---

### Data Models & Network API

#### [MODIFY] [Course.java](file:///d:/Vietsync/VietsyncMobile/app/src/main/java/com/app/learning/data/model/Course.java)
- Add a new field `private User instructor;` with annotations `@SerializedName("instructor")`.
- Implement getter and setter for `instructor`.

#### [MODIFY] [CourseApi.java](file:///d:/Vietsync/VietsyncMobile/app/src/main/java/com/app/learning/data/api/CourseApi.java)
- Add a dynamic search endpoint:
  ```java
  @GET("rest/v1/courses")
  Call<List<Course>> searchCourses(
          @QueryMap Map<String, String> options
  );
  ```
- Add a categories fetch endpoint:
  ```java
  @GET("rest/v1/categories")
  Call<List<Category>> getCategories(
          @Query("select") String select
  );
  ```

#### [NEW] [CourseRepository.java](file:///d:/Vietsync/VietsyncMobile/app/src/main/java/com/app/learning/data/repository/CourseRepository.java)
- Create repository that exposes methods to:
  - Fetch categories from Supabase.
  - Search courses with a map of filters.

---

### Local Database (Search History with Room)

#### [NEW] [SearchHistory.java](file:///d:/Vietsync/VietsyncMobile/app/src/main/java/com/app/learning/data/local/SearchHistory.java)
- Room entity class with `query` (String primary key) and `timestamp` (long).

#### [NEW] [SearchHistoryDao.java](file:///d:/Vietsync/VietsyncMobile/app/src/main/java/com/app/learning/data/local/SearchHistoryDao.java)
- Room DAO with methods to insert/update, delete, clear, and list search history.

#### [NEW] [AppDatabase.java](file:///d:/Vietsync/VietsyncMobile/app/src/main/java/com/app/learning/data/local/AppDatabase.java)
- Room database class that initializes Room and provides the `SearchHistoryDao`.

---

### UI Components & ViewModels

#### [NEW] [SearchViewModel.java](file:///d:/Vietsync/VietsyncMobile/app/src/main/java/com/app/learning/ui/search/SearchViewModel.java)
- Exposes states for query, search suggestions/history, search results, and filters.
- Debounce 300ms using Android `Handler` when performing search queries.
- Manages Room search history insertion, deletion, and retrieval.
- Manages Supabase filter options mapping and network status handling.

#### [NEW] [SearchActivity.java](file:///d:/Vietsync/VietsyncMobile/app/src/main/java/com/app/learning/ui/search/SearchActivity.java)
- Main screen with a `SearchView` inside a premium custom header.
- Integrates `SearchHistoryAdapter` and `SearchResultAdapter`.
- Opens `FilterBottomSheet` when clicking the filter icon.
- Displays loading progress, empty state screen, or results list reactively.

#### [NEW] [FilterBottomSheet.java](file:///d:/Vietsync/VietsyncMobile/app/src/main/java/com/app/learning/ui/search/FilterBottomSheet.java)
- A bottom sheet fragment containing:
  - Categories `ChipGroup` (dynamically filled with categories).
  - Course Levels `ChipGroup` (Beginner, Intermediate, Advanced).
  - Price `RangeSlider` (0 to 5,000,000 VND).
  - Course Durations `ChipGroup` (< 10 hours, 10 - 30 hours, > 30 hours).
  - Apply & Reset buttons.

#### [NEW] [SearchHistoryAdapter.java](file:///d:/Vietsync/VietsyncMobile/app/src/main/java/com/app/learning/ui/search/SearchHistoryAdapter.java)
- Adapter for showing search history list and match suggestions.

#### [NEW] [SearchResultAdapter.java](file:///d:/Vietsync/VietsyncMobile/app/src/main/java/com/app/learning/ui/search/SearchResultAdapter.java)
- Adapter for showing course search results with thumbnail, title, instructor name, rating, and price.

#### [MODIFY] [HomeFragment.java](file:///d:/Vietsync/VietsyncMobile/app/src/main/java/com/app/learning/ui/home/HomeFragment.java)
- Add a click listener to the home screen search input container to launch `SearchActivity` with transition animations.

#### [MODIFY] [AndroidManifest.xml](file:///d:/Vietsync/VietsyncMobile/app/src/main/AndroidManifest.xml)
- Register `com.app.learning.ui.search.SearchActivity` in manifest.

---

### Layouts & Assets

#### [NEW] [activity_search.xml](file:///d:/Vietsync/VietsyncMobile/app/src/main/res/layout/activity_search.xml)
- Custom header with a back button, a search container (with `SearchView`), and a filter icon.
- Two recyclerviews: `rv_search_history` and `rv_search_results` toggling visibility.
- Loading ProgressBar.
- Premium empty state layout (with an icon, warning title, and description).

#### [NEW] [bottom_sheet_filter.xml](file:///d:/Vietsync/VietsyncMobile/app/src/main/res/layout/bottom_sheet_filter.xml)
- Design with scrolling options for categories, levels, price range, durations.
- Material RangeSlider and ChipGroups.

#### [NEW] [item_search_result.xml](file:///d:/Vietsync/VietsyncMobile/app/src/main/res/layout/item_search_result.xml)
- Premium card design matching course results: thumbnail (Glide loaded), title, instructor name, star rating, duration, and formatted price.

#### [NEW] [item_search_history.xml](file:///d:/Vietsync/VietsyncMobile/app/src/main/res/layout/item_search_history.xml)
- Single row layout containing an history icon, query string text, and a delete button.

#### [NEW] [ic_search.xml](file:///d:/Vietsync/VietsyncMobile/app/src/main/res/drawable/ic_search.xml)
- Search icon vector.

#### [NEW] [ic_filter.xml](file:///d:/Vietsync/VietsyncMobile/app/src/main/res/drawable/ic_filter.xml)
- Filter icon vector.

#### [NEW] [bg_search_input.xml](file:///d:/Vietsync/VietsyncMobile/app/src/main/res/drawable/bg_search_input.xml)
- Premium search text field background (rounded corner, grey surface).

---

## Verification Plan

### Automated Build Verification
- Compile and build using the `./gradlew assembleDebug` command to verify that all annotations and code build cleanly.

### Manual Verification Flow
1. Open the App -> Home Screen.
2. Click on the search input box -> verifying it opens `SearchActivity` with a smooth transition.
3. Type queries -> verifying history list shifts to match suggestions (or real-time course search begins).
4. Perform search -> verify course cards render with thumbnail, instructor, rating, price.
5. Click Filter -> bottom sheet opens with correct categories, level chips, price RangeSlider, and duration chips.
6. Apply filter -> verify search results update correctly.
7. Click a search result or back button -> verify correct transitions.
8. Delete individual history queries or clear all history -> verify Room DB local history persistence and removal.
