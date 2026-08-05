package com.app.learning.ui.learning;

import java.util.HashMap;
import java.util.Map;

/**
 * Cung cấp nội dung bài học phù hợp với từng khóa học.
 * Mỗi khóa học và mỗi bài học sẽ có nội dung riêng biệt, không trùng lặp.
 */
public class LessonContentProvider {

    // Map: courseId -> (lessonIndex -> content HTML)
    private static final Map<String, Map<Integer, String>> COURSE_LESSON_CONTENT = new HashMap<>();

    static {
        // =====================================================
        // Khóa 1: Lập trình Android với Java (MVVM)
        // courseId: c0eebc99-9c0b-4ef8-bb6d-6bb9bd380001
        // =====================================================
        Map<Integer, String> androidJavaContent = new HashMap<>();
        androidJavaContent.put(0,
            "<h3>🏗️ Kiến trúc MVVM trong Android</h3>" +
            "<p>MVVM (Model - View - ViewModel) là mẫu kiến trúc được Google khuyến nghị cho Android hiện đại. " +
            "Mô hình này giúp tách biệt logic xử lý khỏi giao diện người dùng.</p>" +
            "<h4>Các thành phần chính:</h4>" +
            "<ul>" +
            "<li><strong>Model</strong>: Lớp dữ liệu (Repository, API, Database)</li>" +
            "<li><strong>View</strong>: Activity/Fragment – chỉ hiển thị dữ liệu</li>" +
            "<li><strong>ViewModel</strong>: Xử lý logic, giữ trạng thái UI</li>" +
            "</ul>" +
            "<pre><code>// ViewModel cơ bản\npublic class CourseViewModel extends AndroidViewModel {\n    private MutableLiveData&lt;List&lt;Course&gt;&gt; courses;\n\n    public LiveData&lt;List&lt;Course&gt;&gt; getCourses() {\n        if (courses == null) {\n            courses = new MutableLiveData&lt;&gt;();\n            loadCourses();\n        }\n        return courses;\n    }\n}</code></pre>"
        );
        androidJavaContent.put(1,
            "<h3>🎨 Thiết kế giao diện với XML Layout</h3>" +
            "<p>Android sử dụng XML để định nghĩa giao diện. ConstraintLayout là layout được khuyến nghị vì hiệu năng cao và linh hoạt.</p>" +
            "<h4>Ví dụ ConstraintLayout:</h4>" +
            "<pre><code>&lt;androidx.constraintlayout.widget.ConstraintLayout\n    android:layout_width=\"match_parent\"\n    android:layout_height=\"match_parent\"&gt;\n\n    &lt;TextView\n        android:id=\"@+id/tvTitle\"\n        android:layout_width=\"0dp\"\n        android:layout_height=\"wrap_content\"\n        app:layout_constraintTop_toTopOf=\"parent\"\n        app:layout_constraintStart_toStartOf=\"parent\"\n        app:layout_constraintEnd_toEndOf=\"parent\"\n        android:text=\"Tiêu đề khóa học\"\n        android:textSize=\"20sp\"/&gt;\n\n&lt;/androidx.constraintlayout.widget.ConstraintLayout&gt;</code></pre>" +
            "<p>💡 <strong>Tip:</strong> Sử dụng DataBinding để kết nối Layout với ViewModel trực tiếp qua biểu thức <code>@{}</code>.</p>"
        );
        androidJavaContent.put(2,
            "<h3>🔗 Tích hợp Supabase với Retrofit</h3>" +
            "<p>Supabase cung cấp REST API tự động từ PostgreSQL. Kết hợp Retrofit để gọi API trong Android.</p>" +
            "<pre><code>// Định nghĩa API Interface\npublic interface CourseApi {\n    @GET(\"courses\")\n    Call&lt;List&lt;Course&gt;&gt; getCourses(\n        @Query(\"select\") String select,\n        @Query(\"order\") String order\n    );\n}\n\n// Khởi tạo Retrofit với Supabase\nRetrofit retrofit = new Retrofit.Builder()\n    .baseUrl(\"https://your-project.supabase.co/rest/v1/\")\n    .addConverterFactory(GsonConverterFactory.create())\n    .build();\n\nCourseApi api = retrofit.create(CourseApi.class);</code></pre>" +
            "<p>⚡ Nhớ thêm header <code>apikey</code> và <code>Authorization</code> cho mỗi request!</p>"
        );
        androidJavaContent.put(3,
            "<h3>⚡ Tối ưu hiệu năng Android</h3>" +
            "<p>Hiệu năng tốt là yếu tố then chốt của ứng dụng chuyên nghiệp. Dưới đây là các kỹ thuật tối ưu quan trọng:</p>" +
            "<h4>1. RecyclerView & DiffUtil</h4>" +
            "<pre><code>// Sử dụng DiffUtil thay vì notifyDataSetChanged()\npublic class CourseDiffCallback extends DiffUtil.Callback {\n    @Override\n    public boolean areItemsTheSame(int old, int nw) {\n        return oldList.get(old).getId()\n            .equals(newList.get(nw).getId());\n    }\n\n    @Override\n    public boolean areContentsTheSame(int old, int nw) {\n        return oldList.get(old).equals(newList.get(nw));\n    }\n}</code></pre>" +
            "<h4>2. ViewHolder Pattern</h4>" +
            "<p>Luôn sử dụng ViewHolder để tránh gọi <code>findViewById()</code> lặp lại trong <code>onBindViewHolder()</code>.</p>"
        );
        COURSE_LESSON_CONTENT.put("c0eebc99-9c0b-4ef8-bb6d-6bb9bd380001", androidJavaContent);

        // =====================================================
        // Khóa 2: UI/UX Design chuyên nghiệp
        // courseId: c0eebc99-9c0b-4ef8-bb6d-6bb9bd380002
        // =====================================================
        Map<Integer, String> uiuxContent = new HashMap<>();
        uiuxContent.put(0,
            "<h3>🎯 Nguyên tắc cơ bản của UI/UX Design</h3>" +
            "<p>UI (User Interface) và UX (User Experience) là hai yếu tố cốt lõi tạo nên sản phẩm số thành công. " +
            "Một thiết kế tốt phải vừa đẹp vừa dễ sử dụng.</p>" +
            "<h4>5 nguyên tắc vàng trong UX:</h4>" +
            "<ol>" +
            "<li>🔍 <strong>Discoverability</strong> – Người dùng dễ tìm thấy tính năng</li>" +
            "<li>📖 <strong>Learnability</strong> – Học cách dùng nhanh chóng</li>" +
            "<li>⚡ <strong>Efficiency</strong> – Thực hiện tác vụ ít bước nhất</li>" +
            "<li>🧠 <strong>Memorability</strong> – Dễ nhớ sau khi đã dùng qua</li>" +
            "<li>😊 <strong>Satisfaction</strong> – Cảm giác thỏa mãn khi dùng</li>" +
            "</ol>" +
            "<pre><code>/* Color Palette chuẩn Material Design */\n--primary: #6750A4;     /* Tím - màu chủ đạo */\n--secondary: #625B71;   /* Tím xám - phụ */\n--surface: #FFFBFE;     /* Trắng sữa - nền */\n--error: #B3261E;       /* Đỏ - lỗi */</code></pre>"
        );
        uiuxContent.put(1,
            "<h3>🖌️ Thiết kế với Figma – Từ Wireframe đến Prototype</h3>" +
            "<p>Figma là công cụ thiết kế số 1 hiện nay, hỗ trợ cộng tác theo thời gian thực.</p>" +
            "<h4>Quy trình thiết kế chuẩn:</h4>" +
            "<pre><code>1. User Research\n   └─ Khảo sát → Phỏng vấn → Persona\n\n2. Information Architecture\n   └─ Site Map → User Flow → Card Sorting\n\n3. Wireframing (Low-fidelity)\n   └─ Sketch thô → Feedback → Iterate\n\n4. Visual Design (High-fidelity)\n   └─ Design System → Components → Screens\n\n5. Prototyping & Testing\n   └─ Interactive prototype → Usability Test</code></pre>" +
            "<p>💡 <strong>Auto Layout</strong> trong Figma cho phép component tự co giãn như CSS Flexbox!</p>"
        );
        uiuxContent.put(2,
            "<h3>📱 Responsive Design cho Mobile</h3>" +
            "<p>Mobile-first design là tiêu chuẩn hiện đại. Thiết kế cho màn hình nhỏ trước, rồi mở rộng ra các kích thước lớn hơn.</p>" +
            "<h4>Breakpoints thông dụng:</h4>" +
            "<pre><code>/* Mobile First Breakpoints */\n/* Default: Mobile 360px - 480px */\n\n@media (min-width: 768px) {\n  /* Tablet */\n  .container { max-width: 720px; }\n}\n\n@media (min-width: 1024px) {\n  /* Desktop */\n  .container { max-width: 960px; }\n}\n\n@media (min-width: 1440px) {\n  /* Large Desktop */\n  .container { max-width: 1200px; }\n}</code></pre>" +
            "<p>🎯 Khoảng cách tối thiểu giữa các nút bấm (touch target): <strong>44x44dp</strong> theo Apple HIG và <strong>48x48dp</strong> theo Material Design.</p>"
        );
        uiuxContent.put(3,
            "<h3>🧪 Usability Testing & Iteration</h3>" +
            "<p>Testing là bước không thể bỏ qua để đảm bảo sản phẩm thực sự phục vụ đúng nhu cầu người dùng.</p>" +
            "<h4>Các phương pháp kiểm tra:</h4>" +
            "<ul>" +
            "<li>🎬 <strong>Think Aloud</strong>: Người dùng nói to suy nghĩ khi thao tác</li>" +
            "<li>📊 <strong>A/B Testing</strong>: So sánh 2 phiên bản thiết kế</li>" +
            "<li>🔥 <strong>Heatmap</strong>: Theo dõi vùng người dùng click nhiều nhất</li>" +
            "<li>📈 <strong>Analytics</strong>: Đo lường conversion rate, bounce rate</li>" +
            "</ul>" +
            "<pre><code>// Ví dụ metrics quan trọng cần theo dõi:\nTask Completion Rate: > 90%  ✅\nError Rate:           < 5%   ✅\nTime on Task:         < 30s  ✅\nUser Satisfaction:    > 4.5  ✅</code></pre>"
        );
        COURSE_LESSON_CONTENT.put("c0eebc99-9c0b-4ef8-bb6d-6bb9bd380002", uiuxContent);

        // =====================================================
        // Khóa 3: Lập trình Android nâng cao với Kotlin
        // courseId: c0eebc99-9c0b-4ef8-bb6d-6bb9bd380003
        // =====================================================
        Map<Integer, String> kotlinContent = new HashMap<>();
        kotlinContent.put(0,
            "<h3>🚀 Kotlin Coroutines & Flow</h3>" +
            "<p>Coroutines là giải pháp xử lý bất đồng bộ hiện đại của Kotlin, thay thế hoàn toàn cho Callback và RxJava.</p>" +
            "<pre><code>// Gọi API bất đồng bộ với Coroutines\nviewModelScope.launch {\n    try {\n        val courses = withContext(Dispatchers.IO) {\n            repository.getCourses()\n        }\n        _uiState.value = UiState.Success(courses)\n    } catch (e: Exception) {\n        _uiState.value = UiState.Error(e.message)\n    }\n}\n\n// Sử dụng Flow để stream dữ liệu\nval courseFlow: Flow&lt;List&lt;Course&gt;&gt; = flow {\n    while (true) {\n        emit(repository.getCourses())\n        delay(30_000) // Refresh mỗi 30 giây\n    }\n}.flowOn(Dispatchers.IO)</code></pre>" +
            "<p>⚡ <code>viewModelScope</code> tự động hủy coroutine khi ViewModel bị destroy!</p>"
        );
        kotlinContent.put(1,
            "<h3>🧹 Clean Architecture trong Android</h3>" +
            "<p>Clean Architecture chia ứng dụng thành các layer độc lập, giúp code dễ test, bảo trì và mở rộng.</p>" +
            "<pre><code>// Cấu trúc thư mục Clean Architecture\napp/\n├── data/\n│   ├── remote/      # API, Retrofit\n│   ├── local/       # Room Database\n│   └── repository/  # Repository Impl\n├── domain/\n│   ├── model/       # Business Models\n│   ├── repository/  # Repository Interface\n│   └── usecase/     # Use Cases\n└── presentation/\n    ├── viewmodel/   # ViewModels\n    └── ui/          # Fragments, Activities\n\n// Ví dụ Use Case\nclass GetCoursesUseCase(private val repo: CourseRepository) {\n    suspend operator fun invoke(): Result&lt;List&lt;Course&gt;&gt; {\n        return repo.getCourses()\n    }\n}</code></pre>"
        );
        kotlinContent.put(2,
            "<h3>💉 Dependency Injection với Hilt</h3>" +
            "<p>Hilt là thư viện DI chính thức của Android, được xây dựng trên Dagger2 nhưng đơn giản hơn nhiều.</p>" +
            "<pre><code>// 1. Thêm annotation @HiltAndroidApp\n@HiltAndroidApp\nclass MyApp : Application()\n\n// 2. Tạo Module cung cấp dependencies\n@Module\n@InstallIn(SingletonComponent::class)\nobject AppModule {\n    @Provides @Singleton\n    fun provideRetrofit(): Retrofit = Retrofit.Builder()\n        .baseUrl(BASE_URL)\n        .build()\n\n    @Provides @Singleton\n    fun provideCourseApi(retrofit: Retrofit): CourseApi\n        = retrofit.create(CourseApi::class.java)\n}\n\n// 3. Inject vào ViewModel\n@HiltViewModel\nclass CourseViewModel @Inject constructor(\n    private val useCase: GetCoursesUseCase\n) : ViewModel()</code></pre>"
        );
        kotlinContent.put(3,
            "<h3>🧪 Unit Testing với JUnit & Mockito</h3>" +
            "<p>Testing là phần không thể thiếu trong phát triển Android chuyên nghiệp. Mục tiêu đạt ít nhất 70% code coverage.</p>" +
            "<pre><code>// Test ViewModel với coroutines\n@ExtendWith(InstantTaskExecutorRule::class)\nclass CourseViewModelTest {\n\n    @MockK\n    private lateinit var useCase: GetCoursesUseCase\n    private lateinit var viewModel: CourseViewModel\n\n    @Before\n    fun setup() {\n        MockKAnnotations.init(this)\n        viewModel = CourseViewModel(useCase)\n    }\n\n    @Test\n    fun `getCourses success should update UI state`() = runTest {\n        // Arrange\n        val mockCourses = listOf(Course(id = \"1\", title = \"Test\"))\n        coEvery { useCase() } returns Result.success(mockCourses)\n\n        // Act\n        viewModel.loadCourses()\n\n        // Assert\n        assertEquals(UiState.Success(mockCourses), viewModel.uiState.value)\n    }\n}</code></pre>"
        );
        COURSE_LESSON_CONTENT.put("c0eebc99-9c0b-4ef8-bb6d-6bb9bd380003", kotlinContent);

        // =====================================================
        // Khóa 4: Tiếng Anh giao tiếp công sở & CNTT
        // courseId: c0eebc99-9c0b-4ef8-bb6d-6bb9bd380004
        // =====================================================
        Map<Integer, String> englishContent = new HashMap<>();
        englishContent.put(0,
            "<h3>💬 Giao tiếp cơ bản trong môi trường CNTT</h3>" +
            "<p>Tiếng Anh là ngôn ngữ chung của ngành IT toàn cầu. Nắm vững các mẫu câu giao tiếp giúp bạn tự tin trong cuộc họp, review code và trao đổi với đồng nghiệp quốc tế.</p>" +
            "<h4>📋 Từ vựng Daily Stand-up Meeting:</h4>" +
            "<pre><code>\"Yesterday, I worked on...\"      → Hôm qua tôi đã làm...\n\"Today, I'm planning to...\"      → Hôm nay tôi dự định...\n\"I'm currently blocked by...\"    → Tôi đang bị chặn bởi...\n\"I need help with...\"            → Tôi cần hỗ trợ về...\n\"I'll finish it by end of day.\"  → Tôi sẽ xong trước cuối ngày.\n\"Let me check and get back to you.\" → Để tôi kiểm tra và phản hồi sau.</code></pre>" +
            "<p>🎯 <strong>Tip:</strong> Sử dụng Present Perfect để nói về công việc đã hoàn thành: <em>\"I have completed the login feature.\"</em></p>"
        );
        englishContent.put(1,
            "<h3>📧 Viết Email chuyên nghiệp trong ngành IT</h3>" +
            "<p>Email là kênh giao tiếp chính thức. Một email tốt cần rõ ràng, ngắn gọn và chuyên nghiệp.</p>" +
            "<h4>Cấu trúc email chuẩn:</h4>" +
            "<pre><code>Subject: [Bug Report] Login page crashes on iOS 17\n\nHi [Name],\n\nI hope this email finds you well.\n\nI'm writing to report a critical bug found in production:\n\n• Issue: App crashes when user taps \"Login\" on iPhone 14\n• Environment: iOS 17.2, App version 2.3.1\n• Steps to reproduce:\n  1. Open the app\n  2. Enter credentials\n  3. Tap \"Login\" button\n• Expected: Navigate to home screen\n• Actual: App crashes with error code NullPointerException\n\nPlease find the crash log attached.\n\nBest regards,\n[Your Name]\nMobile Developer | VietsyncMobile</code></pre>"
        );
        englishContent.put(2,
            "<h3>🤝 Giao tiếp trong Code Review</h3>" +
            "<p>Code Review là kỹ năng thiết yếu. Biết cách đưa ra và nhận phản hồi lịch sự, chuyên nghiệp bằng tiếng Anh.</p>" +
            "<h4>Mẫu câu trong Code Review:</h4>" +
            "<pre><code>// Khi đưa ra góp ý (lịch sự, constructive):\n\"Could we consider using X instead? It might...\"  → Đề xuất\n\"I think this could be simplified by...\"          → Cải thiện\n\"Nit: variable name could be more descriptive.\"   → Minor fix\n\"This looks great! Just one small thing...\"       → Khen + góp ý\n\n// Khi nhận góp ý:\n\"Good catch! I'll fix that.\"           → Cảm ơn + đồng ý\n\"That's a great suggestion, thanks!\"   → Thể hiện sự cởi mở\n\"Could you elaborate on why you suggest X?\" → Hỏi thêm\n\"I had a different approach in mind, which is...\" → Giải thích lý do</code></pre>"
        );
        englishContent.put(3,
            "<h3>🎤 Thuyết trình dự án kỹ thuật (Tech Presentation)</h3>" +
            "<p>Thuyết trình rõ ràng và tự tin là kỹ năng quan trọng giúp bạn thăng tiến trong sự nghiệp IT.</p>" +
            "<h4>Cấu trúc thuyết trình kỹ thuật:</h4>" +
            "<pre><code>// Opening (Mở đầu)\n\"Good morning everyone. Today I'll be presenting...\"\n\"This presentation covers three main points: ...\"\n\n// Problem Statement (Nêu vấn đề)\n\"The current system has a challenge with...\"\n\"We identified a performance bottleneck in...\"\n\n// Solution (Giải pháp)\n\"Our proposed solution is to implement...\"\n\"This approach allows us to... because...\"\n\n// Demo/Results (Kết quả)\n\"As you can see in this demo...\"\n\"After implementation, we achieved a 40% improvement in...\"\n\n// Closing (Kết luận)\n\"To summarize, we have successfully...\"\n\"Are there any questions?\"</code></pre>"
        );
        COURSE_LESSON_CONTENT.put("c0eebc99-9c0b-4ef8-bb6d-6bb9bd380004", englishContent);

        // =====================================================
        // Khóa 5: Khởi nghiệp Thực chiến & Quản trị Kinh doanh
        // courseId: c0eebc99-9c0b-4ef8-bb6d-6bb9bd380005
        // =====================================================
        Map<Integer, String> businessContent = new HashMap<>();
        businessContent.put(0,
            "<h3>💡 Business Model Canvas – Xây dựng mô hình kinh doanh</h3>" +
            "<p>Business Model Canvas (BMC) là công cụ chiến lược giúp bạn hình dung, thiết kế và phân tích mô hình kinh doanh trên một trang giấy.</p>" +
            "<h4>9 khối của BMC:</h4>" +
            "<pre><code>┌─────────────────────────────────────────────────┐\n│ KEY         │ KEY      │ VALUE    │ CUSTOMER  │\n│ PARTNERS    │ ACTIVITIES│ PROPOSITION│ SEGMENTS │\n├─────────────┤          ├──────────┼───────────┤\n│ KEY         │ KEY      │ CHANNELS │ CUSTOMER  │\n│ RESOURCES   │ RESOURCES│          │ RELATION  │\n├─────────────┴──────────┴──────────┴───────────┤\n│ COST STRUCTURE    │    REVENUE STREAMS         │\n└───────────────────┴────────────────────────────┘\n\nVí dụ VietsyncMobile:\n• Value Proposition: Học online linh hoạt, chất lượng cao\n• Customer Segments: Sinh viên CNTT, người đi làm\n• Revenue Streams: Phí đăng ký khóa học, B2B Corporate\n• Key Activities: Tạo nội dung, vận hành platform</code></pre>"
        );
        businessContent.put(1,
            "<h3>📊 Lean Startup – Xây dựng sản phẩm theo MVP</h3>" +
            "<p>Phương pháp Lean Startup giúp giảm thiểu rủi ro khi khởi nghiệp bằng cách kiểm tra giả thuyết với nguồn lực tối thiểu.</p>" +
            "<h4>Vòng Build-Measure-Learn:</h4>" +
            "<pre><code>         BUILD\n           ↓\n    [MVP Product]\n           ↓\n         MEASURE\n           ↓\n    [Data & Metrics]\n       - DAU/MAU\n       - Churn Rate\n       - NPS Score\n           ↓\n          LEARN\n           ↓\n    [Pivot or Persevere?]\n           ↓\n    ↩ Back to BUILD\n\n// KPIs quan trọng cần đo:\nAcquisition  → Bao nhiêu người biết đến sản phẩm?\nActivation   → Bao nhiêu người dùng lần đầu?\nRetention    → Bao nhiêu người quay lại?\nRevenue      → Doanh thu từ mỗi nhóm khách hàng?\nReferral     → Bao nhiêu người giới thiệu bạn bè?</code></pre>"
        );
        businessContent.put(2,
            "<h3>📣 Digital Marketing cho Startup Công nghệ</h3>" +
            "<p>Marketing hiệu quả giúp startup tiếp cận đúng đối tượng với chi phí hợp lý.</p>" +
            "<h4>Chiến lược Growth Hacking:</h4>" +
            "<pre><code>// Funnel Marketing AIDA:\n[AWARENESS]    → SEO, Social Media, Content\n    ↓\n[INTEREST]     → Email Campaign, Webinar\n    ↓\n[DESIRE]       → Case Studies, Testimonials\n    ↓\n[ACTION]       → Free Trial, CTA Buttons\n\n// Ví dụ Content Strategy:\nBlog posts:     2-3 bài/tuần về lập trình\nYouTube:        1 video tutorial/tuần\nFacebook Group: Cộng đồng học viên (UGC)\nEmail:          Newsletter hàng tuần\nLinkedIn:       Thought leadership posts\n\n// CAC vs LTV:\nCAC (Chi phí thu hút khách) &lt; LTV (Giá trị vòng đời) ✅</code></pre>"
        );
        businessContent.put(3,
            "<h3>💰 Tài chính Startup – Từ Seed đến Series A</h3>" +
            "<p>Hiểu rõ tài chính giúp founder đưa ra quyết định đúng đắn và tránh cạn vốn (runway).</p>" +
            "<pre><code>// Các vòng gọi vốn:\nBootstrap     → Tự tài trợ, không pha loãng cổ phần\nFriends & Family → Vốn đầu tiên từ người thân\nSeed Round    → $100K - $2M (Angel/VC nhỏ)\nSeries A      → $2M - $15M (VC lớn)\nSeries B+     → Mở rộng quy mô\n\n// Các chỉ số tài chính cần nắm:\nBurn Rate: $50,000/tháng  → Tốc độ tiêu tiền\nRunway:    18 tháng       → Thời gian còn sống\nMRR:       $30,000        → Monthly Recurring Revenue\nARRR:      $360,000       → Annual Run Rate\nBreak-even: Tháng 24      → Điểm hòa vốn\n\n// Công thức Runway:\nRunway = Số tiền còn lại / Burn Rate hàng tháng</code></pre>"
        );
        COURSE_LESSON_CONTENT.put("c0eebc99-9c0b-4ef8-bb6d-6bb9bd380005", businessContent);
    }

    /**
     * Lấy nội dung HTML cho bài học dựa trên courseId và chỉ số bài học.
     *
     * @param courseId     ID của khóa học
     * @param lessonIndex  Chỉ số bài học (0 = bài 1, 1 = bài 2, ...)
     * @param lessonTitle  Tiêu đề bài học (dùng làm fallback)
     * @return Chuỗi HTML nội dung bài học
     */
    public static String getContent(String courseId, int lessonIndex, String lessonTitle) {
        if (courseId != null && COURSE_LESSON_CONTENT.containsKey(courseId)) {
            Map<Integer, String> lessonMap = COURSE_LESSON_CONTENT.get(courseId);
            if (lessonMap != null && lessonMap.containsKey(lessonIndex)) {
                return lessonMap.get(lessonIndex);
            }
            // Nếu lessonIndex không có sẵn, trả nội dung bài 0
            if (lessonMap != null && !lessonMap.isEmpty()) {
                return lessonMap.values().iterator().next();
            }
        }
        // Fallback khi không tìm được nội dung cụ thể
        return "<h3>📚 " + (lessonTitle != null ? lessonTitle : "Nội dung bài học") + "</h3>" +
               "<p>Bài học này cung cấp kiến thức chuyên sâu về chủ đề liên quan đến khóa học. " +
               "Nội dung được biên soạn bởi các chuyên gia với nhiều năm kinh nghiệm thực chiến.</p>" +
               "<pre><code>// Nội dung đang được cập nhật\n// Vui lòng quay lại sau\nSystem.out.println(\"Coming soon!\");</code></pre>";
    }
}
