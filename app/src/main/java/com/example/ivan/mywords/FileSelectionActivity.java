package com.example.ivan.mywords;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.io.File;

public class FileSelectionActivity extends AppCompatActivity {

    private RecyclerView rvFileList;
    private FileAdapter fAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_selection);

        File directory = new File(MainActivity.DIRECTORY_PATH);
        final File[] files = directory.listFiles();

        rvFileList = (RecyclerView) findViewById(R.id.rv_file_list);
        rvFileList.setLayoutManager(new LinearLayoutManager(this));
        fAdapter = new FileAdapter(files);
        rvFileList.setAdapter(fAdapter);
        fAdapter.setListener(new FileAdapter.OnRecyclerItemClickListener() {
            @Override
            public void onItemClick(int pos) {
                Intent intent = new Intent();
                intent.putExtra(Intent.EXTRA_TEXT, fAdapter.getFilePath(pos));
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}
