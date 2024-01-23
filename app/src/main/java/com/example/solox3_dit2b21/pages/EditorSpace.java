package com.example.solox3_dit2b21.pages;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.solox3_dit2b21.R;
import com.example.solox3_dit2b21.model.Chapter;
import com.example.solox3_dit2b21.model.SubChapter;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.SubMenu;
import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import java.util.Map;

import jp.wasabeef.richeditor.RichEditor;
public class EditorSpace extends AppCompatActivity implements View.OnClickListener{
    private DatabaseReference databaseReference;
    private String bookId;
    private RichEditor mEditor;
//    private TextView mPreview;
    private int chapterId;
    private int subChapterId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle getData = getIntent().getExtras();

        if (getData != null) {
            bookId = getData.getString("bookId");
            // Initialize Firebase
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

            // Update the reference to point to the specific book's chapters
            // Use the bookId to dynamically refer to the correct book chapters
            if (bookId != null && !bookId.isEmpty()) {
                databaseReference = firebaseDatabase.getReference("Chapter");
                fetchChapters(); // Fetch the chapters from Firebase
            } else {
                Log.e("ReadingActivity", "No bookId provided");
                return; // Exit if no bookId is provided
            }
            setContentView(R.layout.activity_editor_space);
            mEditor = (RichEditor) findViewById(R.id.editor);
            mEditor.setEditorHeight(200);
            mEditor.setEditorFontSize(22);
            mEditor.setEditorFontColor(Color.BLACK);
            mEditor.setPadding(10, 10, 10, 10);
            mEditor.setPlaceholder("Insert text here...");

            findViewById(R.id.action_undo).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mEditor.undo();
                }
            });

            findViewById(R.id.action_redo).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mEditor.redo();
                }
            });

            findViewById(R.id.action_bold).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mEditor.setBold();
                }
            });

            findViewById(R.id.action_italic).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mEditor.setItalic();
                }
            });


            findViewById(R.id.action_superscript).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mEditor.setSuperscript();
                }
            });

            findViewById(R.id.action_strikethrough).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mEditor.setStrikeThrough();
                }
            });

            findViewById(R.id.action_underline).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mEditor.setUnderline();
                }
            });

            findViewById(R.id.action_heading1).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mEditor.setHeading(1);
                }
            });

            findViewById(R.id.action_heading2).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mEditor.setHeading(2);
                }
            });

            findViewById(R.id.action_heading3).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mEditor.setHeading(3);
                }
            });

            findViewById(R.id.action_heading4).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mEditor.setHeading(4);
                }
            });

            findViewById(R.id.action_heading5).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mEditor.setHeading(5);
                }
            });

            findViewById(R.id.action_heading6).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mEditor.setHeading(6);
                }
            });

            findViewById(R.id.action_txt_color).setOnClickListener(new View.OnClickListener() {
                private boolean isChanged=false;

                @Override
                public void onClick(View v) {
                    mEditor.setTextColor(isChanged ? Color.BLACK : Color.RED);
                    isChanged = !isChanged;
                }
            });

            findViewById(R.id.action_bg_color).setOnClickListener(new View.OnClickListener() {
                private boolean isChanged;

                @Override
                public void onClick(View v) {
                    mEditor.setTextBackgroundColor(isChanged ? Color.TRANSPARENT : Color.YELLOW);
                    isChanged = !isChanged;
                }
            });

            findViewById(R.id.action_indent).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mEditor.setIndent();
                }
            });

            findViewById(R.id.action_outdent).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mEditor.setOutdent();
                }
            });

            findViewById(R.id.action_align_left).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mEditor.setAlignLeft();
                }
            });

            findViewById(R.id.action_align_center).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mEditor.setAlignCenter();
                }
            });

            findViewById(R.id.action_align_right).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mEditor.setAlignRight();
                }
            });

            findViewById(R.id.action_blockquote).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mEditor.setBlockquote();
                }
            });

            findViewById(R.id.action_insert_bullets).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mEditor.setBullets();
                }
            });

            findViewById(R.id.action_insert_numbers).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mEditor.setNumbers();
                }
            });

            findViewById(R.id.action_insert_checkbox).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mEditor.insertTodo();
                }
            });

        }
    }
    private void fetchChapters() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    List<Chapter> chapters = new ArrayList<>();
                    for (DataSnapshot bookChapterSnapshot : dataSnapshot.getChildren()) {
                        String fetchedBookId = bookChapterSnapshot.child("bookId").getValue(String.class);
                        if (bookId.equals(fetchedBookId)) {
                            for (DataSnapshot chapterSnapshot : bookChapterSnapshot.child("chapters").getChildren()) {
                                Chapter chapter = chapterSnapshot.getValue(Chapter.class);
                                if (chapter != null) {
                                    chapters.add(chapter); // Add chapter to list
                                }
                            }
                            // No need to return here, let the loop finish
                        }
                    }

                    if (!chapters.isEmpty()) {
                        // Handle the first chapter and its subchapters
                        Chapter firstChapter = chapters.get(0);
                        chapterId = firstChapter.getChapterOrder();
                        if (firstChapter.getSubChapters() != null && !firstChapter.getSubChapters().isEmpty()) {
                            // Display the first subchapter of the first chapter
                            SubChapter firstSubChapter = firstChapter.getSubChapters().values().iterator().next();
                            subChapterId=firstSubChapter.getSubChapterOrder();
                            mEditor.setHtml(firstSubChapter.getChapterContent());// This method should reset and update the ViewPager
                            Log.e("chapter", "First subchapter: " + firstSubChapter.getTitle());
                        }
                        Log.e("chapter", chapters.get(0).getTitle());
                        populateDrawerMenu(chapters); // Populate the drawer menu with the complete list of chapters
                    } else {
                        Log.e("ReadingActivity", "No matching bookId found in chapters");
                    }
                } else {
                    Log.e("ReadingActivity", "No chapters available");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("ReadingActivity", "Failed to retrieve chapter data: " + databaseError.getMessage());
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.backButton) {
            finish();
        } else if (v.getId()==R.id.menuButtonEdit) {
            DrawerLayout drawer = findViewById(R.id.drawer_layout_editor);
            drawer.openDrawer(GravityCompat.START);
        }
    }
    private void populateDrawerMenu(List<Chapter> chapters) {
        NavigationView navigationView = findViewById(R.id.nav_view_editor);
        Menu menu = navigationView.getMenu();
        menu.clear(); // Clear any existing items

        for (Chapter chapter : chapters) {
            // Check if subChapters is null or empty and initialize if necessary
            if (chapter.getSubChapters() == null) {
                chapter.setSubChapters(new HashMap<>()); // Initialize with empty Map if null
            }

            SubMenu chapterMenu = menu.addSubMenu(chapter.getTitle());
            for (Map.Entry<String, SubChapter> entry : chapter.getSubChapters().entrySet()) {
                SubChapter subChapter = entry.getValue();
                chapterMenu.add(Menu.NONE, Menu.NONE, Menu.NONE, subChapter.getTitle()).setOnMenuItemClickListener(item -> {
                    // Handle subchapter selection here
                    navigateToSubChapter(subChapter, chapter);
                    return true;
                });
            }
        }
    }

    private void navigateToSubChapter(SubChapter subChapter,Chapter chapter) {
        chapterId=chapter.getChapterOrder();
        if (subChapter != null) {
            subChapterId=subChapter.getSubChapterOrder();
            // Split the subchapter content into pages and update the ViewPager
            mEditor.setHtml(subChapter.getChapterContent()); // This method should reset and update the ViewPager
            Log.d("chapterId",chapterId+"");
            Log.d("subChapterId",subChapterId+"");
            // Optionally, close the drawer if open
            DrawerLayout drawer = findViewById(R.id.drawer_layout_editor);
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            }
        }
    }

}