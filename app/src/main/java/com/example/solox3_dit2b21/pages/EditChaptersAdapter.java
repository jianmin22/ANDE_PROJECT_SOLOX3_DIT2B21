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
        holder.imageViewDeleteToggle.setOnClickListener(v -> deleteChapter(position));
        // Populate subchapters
        populateSubChapters(holder, chapter,position);


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

    private void populateSubChapters(ChapterViewHolder holder, Chapter chapter,int chapterPosition) {
        holder.linearLayoutSubchapters.removeAllViews();
        if (chapter.getSubChapters() != null) {
            for (Map.Entry<String, SubChapter> entry : chapter.getSubChapters().entrySet()) {
                SubChapter subChapter = entry.getValue();

                // Create a horizontal LinearLayout
                LinearLayout horizontalLayout = new LinearLayout(holder.itemView.getContext());
                horizontalLayout.setOrientation(LinearLayout.HORIZONTAL);
                horizontalLayout.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));

                // EditText for subchapter title
                EditText editTextSubChapterTitle = new EditText(holder.itemView.getContext());
                editTextSubChapterTitle.setText(subChapter.getTitle());
                editTextSubChapterTitle.setTextSize(16);
                editTextSubChapterTitle.setLayoutParams(new LinearLayout.LayoutParams(
                        0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)); // Weighted to take up remaining space
                horizontalLayout.addView(editTextSubChapterTitle);

                // ImageView for adding a subchapter
                ImageView imageViewAddSubChapter = new ImageView(holder.itemView.getContext());
                imageViewAddSubChapter.setImageResource(R.drawable.ic_add); // Assuming you have an add icon
                imageViewAddSubChapter.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                imageViewAddSubChapter.setOnClickListener(v -> addSubChapter(chapter, chapterPosition));
                horizontalLayout.addView(imageViewAddSubChapter);

                // ImageView for deleting a subchapter
                ImageView imageViewDeleteSubChapter = new ImageView(holder.itemView.getContext());
                imageViewDeleteSubChapter.setImageResource(R.drawable.ic_trash); // Assuming you have a delete icon
                imageViewDeleteSubChapter.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                horizontalLayout.addView(imageViewDeleteSubChapter);

                holder.linearLayoutSubchapters.addView(horizontalLayout); // Add the horizontal layout to the vertical layout

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
                imageViewDeleteSubChapter.setOnClickListener(v -> {
                    // Remove subChapter
                    chapter.getSubChapters().remove(entry.getKey());
                    // Notify data set changed or handle UI update accordingly
                    notifyDataSetChanged(); // Call this if your UI needs to reflect the change immediately
                });
            }
        }

    }

    private void addSubChapter(Chapter chapter, int chapterPosition) {
        int newSubChapterOrder = chapter.getSubChapters().size() + 1;
        SubChapter newSubChapter = new SubChapter("SubChapter " + newSubChapterOrder, newSubChapterOrder, "");
        // Adjust key generation as needed to ensure uniqueness
        chapter.getSubChapters().put("subchapter" + newSubChapterOrder, newSubChapter);
        notifyItemChanged(chapterPosition); // Update only the modified chapter
    }

    private void deleteSubChapter(Chapter chapter, SubChapter subChapter) {
        chapter.getSubChapters().values().remove(subChapter);
        notifyDataSetChanged(); // Notify the adapter to update the UI
    }

    private void deleteChapter(int position) {
        chapters.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, chapters.size());
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
