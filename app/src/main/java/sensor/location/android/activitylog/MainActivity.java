package sensor.location.android.activitylog;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.io.File;


public class MainActivity extends Activity implements View.OnClickListener {
    final Context context = this;

    Button emailBtn;
    Button deleteBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(Build.VERSION.SDK_INT >25){
            startForegroundService(new Intent(this, Background.class));
        }else{
            startService(new Intent(this, Background.class));
        }
        emailBtn = findViewById(R.id.emailBtn);
        emailBtn.setOnClickListener(this);
        deleteBtn = findViewById(R.id.deleteBtn);
        deleteBtn.setOnClickListener(this);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.emailBtn:
                emailMe(null);
                break;
            case R.id.deleteBtn:
                DeleteFiles(null);
                break;
        }
    }


    public void emailMe(View v) {

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        String directory = (String.valueOf(context.getFilesDir()) + File.separator);
        Log.i("Directory", directory);

        File file = new File(directory + File.separator + "data.txt");

        if (file.exists()) {
            Log.i("File exists", "true");
        } else {
            Log.i("File doesn't exist", "true");
        }
        Uri uri;
        try {
            uri = FileProvider.getUriForFile(context, "sensor.location.android.activitylog.fileprovider", file);
            intent.putExtra(Intent.EXTRA_STREAM, uri);
        } catch (Exception e) {
            Log.e("File upload error", "Error:" + e);
        }

        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        context.startActivity(intent);
        Log.d("from main", "Worked fine");


    }

    public void DeleteFiles(View view) {

        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.search_prompt, null);
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setView(promptsView);

        final EditText userInput = promptsView.findViewById(R.id.user_input);


        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setNegativeButton("Go",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // DO THE METHOD HERE WHEN PROCEED IS CLICKED*/
                                String user_text = (userInput.getText()).toString();

                                // CHECK FOR USER'S INPUT **/
                                if (user_text.equals("oeg")) {
                                    String directory = (String.valueOf(context.getFilesDir()));
                                    Log.i("Directory from delete", directory);

                                    File file = new File(directory + File.separator + "data.txt");
                                    Log.i("Couldn't directory", "" + file);
                                    if (file.exists()) {
                                        try {
                                            deleteFile("data.txt");
                                            Toast.makeText(context, "File deleted.", Toast.LENGTH_SHORT).show();
                                        } catch (Exception e) {
                                            Log.e("Couldn't delete file", "" + e);
                                        }
                                    } else {
                                        Toast.makeText(context, "File not found", Toast.LENGTH_SHORT).show();
                                    }

                                } else {
                                    Toast.makeText(context, "Password incorrect", Toast.LENGTH_SHORT).show();
                                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                    builder.setTitle("Error");
                                    builder.setPositiveButton("Cancel", null);
                                    builder.setNegativeButton("Retry", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id) {
                                        }
                                    });
                                    builder.create().show();

                                }
                            }
                        })
                .setPositiveButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        }
                );

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        startService(new Intent(this, Background.class));
    }
}
