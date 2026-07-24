package com.app.learning.ui.learning;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.vietsyncmobile.R;

public class LessonContentFragment extends Fragment {

    private TextView tvLessonTitle;
    private WebView webViewContent;

    public static LessonContentFragment newInstance(String title, String content) {
        LessonContentFragment fragment = new LessonContentFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("content", content);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_lesson_content, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvLessonTitle = view.findViewById(R.id.tvLessonTitle);
        webViewContent = view.findViewById(R.id.webViewContent);

        WebSettings settings = webViewContent.getSettings();
        settings.setJavaScriptEnabled(true);

        String title = getArguments() != null ? getArguments().getString("title") : "Nội dung bài học";
        String content = getArguments() != null ? getArguments().getString("content") : "<p>Chào mừng bạn đến với bài học.</p>";

        tvLessonTitle.setText(title);

        String styledHtml = "<html><head><style>" +
                "body { color: #E2E8F0; background-color: #0F172A; font-family: sans-serif; line-height: 1.6; padding: 8px; }" +
                "pre { background: #1E293B; padding: 12px; border-radius: 8px; overflow-x: auto; color: #60A5FA; font-family: monospace; }" +
                "code { background: #1E293B; color: #38BDF8; padding: 2px 6px; border-radius: 4px; }" +
                "img { max-width: 100%; height: auto; border-radius: 8px; }" +
                "</style></head><body>" + content + "</body></html>";

        webViewContent.loadDataWithBaseURL(null, styledHtml, "text/html", "UTF-8", null);
    }
}
