package vn.edu.vnuk.vnuk_sharing.Functional;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import vn.edu.vnuk.vnuk_sharing.Data;
import vn.edu.vnuk.vnuk_sharing.GetInfoOfFileFromUri;
import vn.edu.vnuk.vnuk_sharing.R;

public class SyllabusScreen extends AppCompatActivity implements View.OnClickListener {

    //this is the pic pdf code used in file chooser
    final static int PICK_PDF_CODE = 2342;

    StorageReference mStorageReference = FirebaseStorage.getInstance().getReference();
    DatabaseReference mDatabaseReference;

    TextView lb_size_file, lb_name_file;
    TextView textViewStatus;
    Button btnCreateOrUpdate;
    ImageView imageViewChooseFile;

    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_syllabus_screen);

        Intent callFunctionalScreen = new Intent();

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Syllabus");

        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        textViewStatus = (TextView) findViewById(R.id.textViewStatus);
        lb_name_file = (TextView) findViewById(R.id.lb_name_file);
        lb_size_file = (TextView) findViewById(R.id.lb_size_file);
        btnCreateOrUpdate = (Button) findViewById(R.id.btnCreateOrUpdate);
        btnCreateOrUpdate.setOnClickListener(this);
        imageViewChooseFile = (ImageView) findViewById(R.id.imageView);
        imageViewChooseFile.setOnClickListener(this);

        if(Data.currentSyllabus.getExists() == false) {
            btnCreateOrUpdate.setText("Create");
        }else{
            btnCreateOrUpdate.setText("Update");
        }

    }

    @Override
    public void onStart(){
        super.onStart();



    }

    public void onBackPressed() {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setTitle("LOG OUT");
        alertBuilder.setMessage("Uploading Syllabus, are you sure to back?");
        alertBuilder.setPositiveButton("TAKE ME AWAY", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int id) {
                finish();
                Intent i=new Intent();
                i.putExtra("finish", true);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                finish();
            }
        });
        alertBuilder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog alertDialog = alertBuilder.create();
        alertDialog.show();
    }

    //this function will get the pdf from the storage
    private void getPDF() {
        //for greater than lolipop versions we need the permissions asked on runtime
        //so if the permission is not available user will go to the screen to allow storage permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.parse("package:" + getPackageName()));
            startActivity(intent);
            return;
        }

        //creating an intent for file chooser
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_PDF_CODE);
    }

    GetInfoOfFileFromUri getInfoOfFileFromUri = new GetInfoOfFileFromUri();

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //when the user choses the file
        if (requestCode == PICK_PDF_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            //if a file is selected
            if (data.getData() != null) {
                //uploading the file

                getInfoOfFileFromUri.get(getApplicationContext(), data.getData());
                lb_name_file.setText(getInfoOfFileFromUri.getNameFile());
                lb_size_file.setText(String.valueOf(getInfoOfFileFromUri.getSizeFile()));

                //Toast.makeText(getApplicationContext(), getInfoOfFileFromUri.getNameFile() + "\n" + String.valueOf(getInfoOfFileFromUri.getSizeFile()), Toast.LENGTH_LONG).show();
                uploadFile(data.getData());
            }else{
                Toast.makeText(this, "No file chosen", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void uploadFile(Uri data) {
        StorageReference sRef = mStorageReference.child("Syllabus" + "-" + Data.currentSyllabus.getIdCourse() + ".pdf");
        StorageTask<UploadTask.TaskSnapshot> a = sRef.putFile(data)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @SuppressWarnings("VisibleForTests")
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        progressBar.setVisibility(View.GONE);
                        textViewStatus.setText("File Uploaded Successfully");

                        FirebaseDatabase.getInstance().getReference().child("root").child("syllabuses").child("syllabus" + "-" + Data.currentSyllabus.getIdCourse()).child("exists").setValue(true);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @SuppressWarnings("VisibleForTests")
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        textViewStatus.setText((int) progress + "% Uploading...");
                    }
                });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.imageView : {
                getPDF();
            }
            break;
        }
    }
}
