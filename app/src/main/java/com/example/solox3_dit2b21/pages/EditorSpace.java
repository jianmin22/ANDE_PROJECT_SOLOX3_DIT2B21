package com.example.solox3_dit2b21.pages;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.solox3_dit2b21.R;
import com.example.solox3_dit2b21.Utils.AuthUtils;
import com.example.solox3_dit2b21.dao.BookDao;
import com.example.solox3_dit2b21.dao.ChapterDao;
import com.example.solox3_dit2b21.dao.DataCallback;
import com.example.solox3_dit2b21.dao.DataStatusCallback;
import com.example.solox3_dit2b21.daoimpl.FirebaseBookDao;
import com.example.solox3_dit2b21.daoimpl.FirebaseChapterDao;
import com.example.solox3_dit2b21.model.Book;
import com.example.solox3_dit2b21.model.Chapter;
import com.example.solox3_dit2b21.model.SubChapter;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;

import android.graphics.Color;
import android.util.Log;
import android.view.Menu;
import android.view.SubMenu;
import android.view.View;

import java.util.HashMap;
import java.util.List;
import android.widget.Toast;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import java.util.Map;
import java.util.Objects;

import jp.wasabeef.richeditor.RichEditor;
public class EditorSpace extends AppCompatActivity implements View.OnClickListener{
    private DatabaseReference databaseReference;
    private String bookId;
    private RichEditor mEditor;
    private SubChapter currentSubChapter;
    private int chapterId;
    private int subChapterId;
    private String editorContent;
    private BookDao bookDao = new FirebaseBookDao();
    private List<Chapter> chapters;
    private ChapterDao chapterDao=new FirebaseChapterDao();
    private String userId;

    @Override
    protected void onStart() {
        super.onStart();
        AuthUtils.redirectToLoginIfNotAuthenticated(this);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle getData = getIntent().getExtras();
        userId=AuthUtils.getUserId();

        if (getData != null) {
            bookId = getData.getString("bookId");

            if (bookId != null && !bookId.isEmpty()) {
                bookDao.loadBookDetailsById(bookId, new DataCallback<Book>() {
                    @Override
                    public void onDataReceived(Book returnedBookDetails) {
                        if (returnedBookDetails!=null) {
                            if(!Objects.equals(returnedBookDetails.getAuthorId(), userId)){
                                Toast.makeText(getApplicationContext(), "Failed to load page", Toast.LENGTH_LONG).show();
                                finish();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Failed to load page", Toast.LENGTH_LONG).show();
                            finish();
                        }
                    }

                    @Override
                    public void onError(Exception exception) {
                        Toast.makeText(getApplicationContext(), "Failed to load page", Toast.LENGTH_LONG).show();
                        Log.e("Firebase", "Failed to get book details", exception);
                        finish();
                    }
                });
                fetchChapters();
            } else {
                Log.e("ReadingActivity", "No bookId provided");
                return;
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
        chapterDao.fetchChapters(bookId, new DataCallback<List<Chapter>>() {
            @Override
            public void onDataReceived(List<Chapter> data) {
                chapters = data;

                if (!chapters.isEmpty()) {
                    // Handle the first chapter and its subchapters
                    Chapter firstChapter = chapters.get(0);
                    chapterId = firstChapter.getChapterOrder();
                    if (firstChapter.getSubChapters() != null && !firstChapter.getSubChapters().isEmpty()) {
                        // Display the first subchapter of the first chapter
                        SubChapter firstSubChapter = firstChapter.getSubChapters().values().iterator().next();
                        subChapterId = firstSubChapter.getSubChapterOrder();
                        mEditor.setHtml(firstSubChapter.getChapterContent());// This method should reset and update the ViewPager
                        currentSubChapter = firstSubChapter;
                        mEditor.setOnTextChangeListener(new RichEditor.OnTextChangeListener() {
                            @Override
                            public void onTextChange(String text) {
                                editorContent = text;
                                Log.d("text", text);
                                if (currentSubChapter != null) {
                                    currentSubChapter.setChapterContent(editorContent);
                                    Log.d("current subchapter", currentSubChapter.getChapterContent());
                                }
                            }
                        });
                        Log.d("chapter", "First subchapter: " + firstSubChapter.getTitle());
                    }
                    Log.d("chapter", chapters.get(0).getTitle());
                    populateDrawerMenu(chapters); // Populate the drawer menu with the complete list of chapters
                } else {
                    Log.e("ReadingActivity", "No chapters available");
                }
            }


            @Override
            public void onError(Exception exception) {
                Log.e("ReadingActivity", "Failed to retrieve chapter data: " + exception.getMessage());
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
        }else if(v.getId()==R.id.saveChangesButton){
            chapterDao.saveChapters(bookId, chapters, new DataStatusCallback() {
                @Override
                public void onSuccess() {
                    Toast.makeText(EditorSpace.this, "Chapter saved successfully", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Exception exception) {
                    Log.e("EditChapter", "error occurred"+exception.getMessage());
                    Toast.makeText(EditorSpace.this, "Failed to save chapter" , Toast.LENGTH_SHORT).show();
                }
            });

        }else if (v.getId()==R.id.back){
            finish();
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
        currentSubChapter = subChapter;
        if (subChapter != null) {
            subChapterId=subChapter.getSubChapterOrder();
            // Split the subchapter content into pages and update the ViewPager
            mEditor.setHtml(subChapter.getChapterContent()); // This method should reset and update the ViewPager
            mEditor.setOnTextChangeListener(new RichEditor.OnTextChangeListener() {
                @Override
                public void onTextChange(String text) {
                    editorContent = text;
                    Log.d("text", text);
                    if (currentSubChapter != null) {
                        currentSubChapter.setChapterContent(editorContent);
                        Log.d("current subchapter",currentSubChapter.getChapterContent());
                    }
                }
            });
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