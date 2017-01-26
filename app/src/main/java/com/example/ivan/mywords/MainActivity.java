package com.example.ivan.mywords;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaScannerConnection;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String DIRECTORY_PATH = Environment.getExternalStorageDirectory().toString() + "/myWords/";
    public static final String FILE_NAME = "words.txt";
    public static final String PATH_PREF_KEY = "filepath";

//    public static final String FILE_PATH = Environment.getExternalStorageDirectory().toString() + "/myWords/words.txt";

    private int position;

    private TextView tvWord;

    private String TAG = "tag";

    private List<String[]> wordsWithTranslations;
    private boolean translationVisibility = false;
    private boolean isInverted = false;

//    private String currentFilePath = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        File rootDirectory = new File(DIRECTORY_PATH);
        if (!rootDirectory.exists() || !rootDirectory.isDirectory()) {
            boolean result = rootDirectory.mkdirs();
            Log.d(TAG, "onCreate: " + result);
            MediaScannerConnection.scanFile(this, new String[]{DIRECTORY_PATH}, null, null);
        }
        File file = new File(DIRECTORY_PATH + FILE_NAME);
        if (!file.exists() || !file.isFile()) {
            try {
                file.createNewFile();
                FileWriter writer = new FileWriter(file);
                writer.append("forest - лес");
                writer.flush();
                writer.close();
            } catch (IOException e) {
                Log.d(TAG, "onCreate: creating exception");
                e.printStackTrace();
            }
        }



        tvWord = (TextView) findViewById(R.id.tv_word);
        tvWord.setOnClickListener(this);
        update();
        findViewById(R.id.activity_main).setOnClickListener(this);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                position--;
                if (position == -1) {
                    position = wordsWithTranslations.size() - 1;
                }
                translationVisibility = false;
                showWord(position);
                break;
            case R.id.action_invert:
                isInverted = !isInverted;
                showWord(position);
                break;
            case R.id.action_update:
                update();
                break;
            case R.id.action_delete:
                deletePosition(position);
                break;
            case R.id.action_add:
                newWord();
                break;
            case R.id.action_select_file:
                selectFile();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            SharedPreferences sPref = getPreferences(MODE_PRIVATE);
            SharedPreferences.Editor editor = sPref.edit();
            editor.putString(PATH_PREF_KEY, data.getStringExtra(Intent.EXTRA_TEXT));
            editor.apply();
            update();
        }
    }

    private String readFile(String filePath){
        Log.d(TAG, "readFile: " + filePath);
        try {
            File file = new File(filePath);
            if (file.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                StringBuilder builder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
                reader.close();
                return builder.toString();
            }
        } catch (FileNotFoundException e) {
            Log.d(TAG, "readFile: exception");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void newWord() {
        final EditText etNewWord = new EditText(this);
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int id) {
                switch (id) {
                    case DialogInterface.BUTTON_POSITIVE:
                        String newWord = etNewWord.getText().toString();
                        if (TextUtils.isEmpty(newWord)) {
                            Toast.makeText(MainActivity.this, "field is empty", Toast.LENGTH_SHORT).show();
                        } else {
                            if (newWord.contains(",")){
                                Toast.makeText(MainActivity.this, "contains ',' ", Toast.LENGTH_SHORT).show();
                            } else {
                                writeWordToFile(newWord);
                                wordsWithTranslations = getWordsList();
                                dialogInterface.dismiss();
                            }
                        }
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        dialogInterface.dismiss();
                        break;
                }
            }
        };
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("new Word");
        dialog.setView(etNewWord);
        dialog.setPositiveButton("ok", listener);
        dialog.setNegativeButton("cancel", listener);
        dialog.show();

    }

    private void writeWordToFile(String word){
        Log.d(TAG, "writeWordToFile: ");
        String text = readFile(getCurrentFilePath());
        try {
            FileWriter writer = new FileWriter(new File(DIRECTORY_PATH, FILE_NAME));
            writer.append(text).append(",").append(word);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            Log.d(TAG, "writeWordToFile: "+ e.getMessage());
            e.printStackTrace();
        }
    }

    private List<String[]> getWordsList() {
        String text = readFile(getCurrentFilePath());
        if (text != null) {
            String[] words = text.split(",");
            List<String> wordsArray = Arrays.asList(words);
            List<String[]> separatedWordsArray = new ArrayList<>();
            int j;
            for (j = 0; j< wordsArray.size(); j++) {
                separatedWordsArray.add(j, wordsArray.get(j).split("-"));
            }
            return separatedWordsArray;
        } else {
            Log.d(TAG, "reading error");
            return null;
        }
    }

    private void showWord(int position){
        String[] message = wordsWithTranslations.get(position);
        String text;

        if (isInverted) {
            text = message[1];
        } else {
            text = message[0];
        }

        if (translationVisibility) {
            if (isInverted) {
                text += "-" + message[0];
            } else {
                text += "-" + message[1];
            }
        }
        tvWord.setText(text);
    }

    private void deletePosition(final int pos) {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle("delete word");
        String[] text = wordsWithTranslations.get(pos);
        dialogBuilder.setMessage("Word <" + text[0] + "-" + text[1] + "> will be deleted. Continue?");
        dialogBuilder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                wordsWithTranslations.remove(pos);
                if (position >= wordsWithTranslations.size()) {
                    position = wordsWithTranslations.size() - 1;
                }
                showWord(position);
                Toast.makeText(MainActivity.this, "word deleted", Toast.LENGTH_SHORT).show();
                dialogInterface.dismiss();
            }
        });
        dialogBuilder.setNegativeButton("no", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        dialogBuilder.show();
    }

    private String getCurrentFilePath(){
        SharedPreferences sPref = getPreferences(MODE_PRIVATE);
        return sPref.getString(PATH_PREF_KEY, DIRECTORY_PATH + FILE_NAME);
    }

    private void update(){
        setTitle((new File(getCurrentFilePath())).getName());
        wordsWithTranslations = getWordsList();
        if (wordsWithTranslations != null) {
            if (position >= wordsWithTranslations.size()) {
                position = 0;
            }
            showWord(position);
        } else {
            Toast.makeText(this, "can't open file", Toast.LENGTH_SHORT).show();
            selectFile();
        }
    }

    private void selectFile() {
        startActivityForResult(new Intent(this, FileSelectionActivity.class), 101);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.activity_main:
                Log.d(TAG, "onClick: activity");
                position++;
                if (position == wordsWithTranslations.size()) {
                    position = 0;
                }
                translationVisibility = false;
                showWord(position);
                break;
            case R.id.tv_word:
                Log.d(TAG, "onClick: tvWord");
                translationVisibility = !translationVisibility;
                showWord(position);
                break;
        }
    }
}
