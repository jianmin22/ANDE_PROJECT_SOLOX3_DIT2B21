package com.example.solox3_dit2b21.pages;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.collection.CircularArray;
import androidx.recyclerview.widget.RecyclerView;

import com.example.solox3_dit2b21.R;
import com.example.solox3_dit2b21.model.Chapter;
import com.example.solox3_dit2b21.model.SubChapter;

import java.util.List;
import java.util.Map;

public class EditChaptersAdapter extends RecyclerView.Adapter<EditChaptersAdapter.ChapterViewHolder> {

    private List<Chapter> chapters;
    private LayoutInflater inflater;

    public EditChaptersAdapter(Context context, List<Chapter> chapters) {
        this.inflater = LayoutInflater.from(context);
        this.chapters = chapters;
    }

    @NonNull
    @Override
    public ChapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.item_chapter, parent, false);
        return new ChapterViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull ChapterViewHolder holder, int position) {
        Chapter chapter = chapters.get(position);
        holder.textViewChapterTitle.setText(chapter.getTitle());

        holder.imageViewExpandToggle.setOnClickListener(v -> {
            boolean isExpanded = holder.linearLayoutSubchapters.getVisibility() == View.VISIBLE;
            holder.linearLayoutSubchapters.setVisibility(isExpanded ? View.GONE : View.VISIBLE);
            holder.imageViewExpandToggle.setImageResource(isExpanded ? R.drawable.ic_expand_more : R.drawable.ic_expand_less);
        });

        // Clear any existing subchapter views to avoid duplicating views when the list is updated
        holder.linearLayoutSubchapters.removeAllViews();

        // Populate subchapters if they exist
        if (chapter.getSubChapters() != null) {
            for (Map.Entry<String, SubChapter> entry : chapter.getSubChapters().entrySet()) {
                SubChapter subChapter = entry.getValue();
                TextView subChapterView = new TextView(holder.itemView.getContext());
                subChapterView.setText(subChapter.getTitle());
                subChapterView.setTextSize(16);
                // Padding, margin, and any other layout parameters can be set here
                // You can also set a click listener to edit/delete the subchapter
                holder.linearLayoutSubchapters.addView(subChapterView);
            }
        }
    }


    @Override
    public int getItemCount() {
        return chapters.size();
    }

    public static class ChapterViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewChapterTitle;
        public ImageView imageViewExpandToggle, imageViewDeleteToggle, imageViewAddToggle;
        public LinearLayout linearLayoutSubchapters;

        public ChapterViewHolder(View itemView) {
            super(itemView);
            textViewChapterTitle = itemView.findViewById(R.id.textView_chapter_title);
            imageViewExpandToggle = itemView.findViewById(R.id.imageView_expand_toggle);
            imageViewDeleteToggle = itemView.findViewById(R.id.imageView_delete_toggle);
            imageViewAddToggle = itemView.findViewById(R.id.imageView_add_toggle);
            linearLayoutSubchapters = itemView.findViewById(R.id.linearLayout_subchapters);
        }
    }
}


