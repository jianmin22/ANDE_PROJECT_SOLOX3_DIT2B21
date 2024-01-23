package com.example.solox3_dit2b21.pages;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.solox3_dit2b21.R;
import com.example.solox3_dit2b21.model.Chapter;
import com.example.solox3_dit2b21.model.SubChapter;

import java.util.List;
import java.util.Map;

public class EditChaptersAdapter extends RecyclerView.Adapter<EditChaptersAdapter.ChapterViewHolder> {

    private final List<Chapter> chapters;
    private final LayoutInflater inflater;

    public EditChaptersAdapter(Context context, List<Chapter> chapters) {
        this.inflater = LayoutInflater.from(context);
        this.chapters = chapters;
    }

    @NonNull
    @Override
    public ChapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.item_chapter, parent, false);
        return new ChapterViewHolder(itemView,(EditChapter) inflater.getContext());
    }

    @Override
    public void onBindViewHolder(@NonNull ChapterViewHolder holder, int position) {
        Chapter chapter = chapters.get(position);
        holder.editTextChapterTitle.setText(chapter.getTitle());

        // Set up TextWatcher for chapter title
        setupChapterTitleTextWatcher(holder, chapter);

        holder.imageViewExpandToggle.setOnClickListener(v -> toggleSubChapterVisibility(holder));

        // Populate subchapters
        populateSubChapters(holder, chapter);
    }

    private void setupChapterTitleTextWatcher(ChapterViewHolder holder, Chapter chapter) {
        holder.editTextChapterTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                // Not needed
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                // Update the chapter title as it is edited
                chapter.setTitle(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Not needed
            }
        });
    }

    private void toggleSubChapterVisibility(ChapterViewHolder holder) {
        boolean isExpanded = holder.linearLayoutSubchapters.getVisibility() == View.VISIBLE;
        holder.linearLayoutSubchapters.setVisibility(isExpanded ? View.GONE : View.VISIBLE);
        holder.imageViewExpandToggle.setImageResource(isExpanded ? R.drawable.ic_expand_more : R.drawable.ic_expand_less);
    }

    private void populateSubChapters(ChapterViewHolder holder, Chapter chapter) {
        holder.linearLayoutSubchapters.removeAllViews();
        if (chapter.getSubChapters() != null) {
            for (Map.Entry<String, SubChapter> entry : chapter.getSubChapters().entrySet()) {
                SubChapter subChapter = entry.getValue();

                EditText editTextSubChapterTitle = new EditText(holder.itemView.getContext());
                editTextSubChapterTitle.setText(subChapter.getTitle());
                editTextSubChapterTitle.setTextSize(16);
                // Add other styling and layout parameters as needed
                holder.linearLayoutSubchapters.addView(editTextSubChapterTitle);
// Create new image views for each subchapter
                ImageView imageViewAddSubChapter = new ImageView(holder.itemView.getContext());
                imageViewAddSubChapter.setImageResource(R.drawable.ic_add); // Assuming you have an add icon
                holder.linearLayoutSubchapters.addView(imageViewAddSubChapter);

                ImageView imageViewDeleteSubChapter = new ImageView(holder.itemView.getContext());
                imageViewDeleteSubChapter.setImageResource(R.drawable.ic_trash); // Assuming you have a delete icon
                holder.linearLayoutSubchapters.addView(imageViewDeleteSubChapter);
                // Set up TextWatcher for subchapter title
                editTextSubChapterTitle.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                        // Not needed
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                        // Update the subchapter title as it is edited
                        subChapter.setTitle(charSequence.toString());
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        // Not needed
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return chapters.size();
    }

    public static class ChapterViewHolder extends RecyclerView.ViewHolder {
        public EditText editTextChapterTitle;
        public ImageView imageViewExpandToggle, imageViewDeleteToggle, imageViewAddToggle,imageViewAddSubChapterToggle,imageViewDeleteSubChapterToggle;
        public LinearLayout linearLayoutSubchapters;

        public ChapterViewHolder(View itemView,EditChapter editChapterActivity) {
            super(itemView);
            editTextChapterTitle = itemView.findViewById(R.id.editText_chapter_title);
            imageViewExpandToggle = itemView.findViewById(R.id.imageView_expand_toggle);
            imageViewDeleteToggle = itemView.findViewById(R.id.imageView_delete_toggle);
            imageViewAddToggle = itemView.findViewById(R.id.imageView_add_toggle);
            imageViewAddToggle.setOnClickListener(v -> {
                editChapterActivity.addNewChapter();
            });
            imageViewAddSubChapterToggle= itemView.findViewById(R.id.imageView_add_subchapter);
            imageViewDeleteSubChapterToggle= itemView.findViewById(R.id.imageView_delete_subchapter);
            linearLayoutSubchapters = itemView.findViewById(R.id.linearLayout_subchapters);
        }
    }
}
