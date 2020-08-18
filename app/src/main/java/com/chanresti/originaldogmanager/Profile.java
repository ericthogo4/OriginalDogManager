package com.chanresti.originaldogmanager;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

public class Profile extends AppCompatActivity {

    private int PICK_IMAGE_REQUEST = 1;
    public Uri DogPicUri;
    ListView mListView;
    public boolean iseditopp;
    Cursor Activecursor;
private DogsDbAdapter mDbAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mListView=findViewById(R.id.dogdetails);
        mDbAdapter=new DogsDbAdapter(this);

        setdefaults();
        mDbAdapter.open();
        Button button=findViewById(R.id.button4);
        Button buttondel=findViewById(R.id.buttondel);
        buttondel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Activecursor!=null){
                showDeleteDialog();
                }
                else {
                    Toast.makeText(getApplicationContext(),"please view or add a dogfile first",Toast.LENGTH_LONG).show();
                }
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Activecursor!=null){
                    iseditopp=true;
                    showImagePicker();
                }
                else {
                    Toast.makeText(getApplicationContext(),"please view or add a dogfile first",Toast.LENGTH_LONG).show();
                }
            }
        });

    }
//a method to show a dialog to confirm if a user wants to delete a dog file and delete it
    private void showDeleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Profile.this);
        builder.setTitle("Delete");
        builder.setMessage("Are You Sure you want to delete this dogfile?");
        builder.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                mDbAdapter.deleteDogById(Activecursor.getInt(0));
                Toast.makeText(getApplicationContext(),"DogFile Deleted Successfully",Toast.LENGTH_SHORT).show();
                setdefaults();
                dialogInterface.dismiss();
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        final Dialog dialog = builder.create();
        dialog.show();
    }
// a method used to set all views to their default states
    private void setdefaults() {
        ImageView imageView = (ImageView) findViewById(R.id.ImageView);
        imageView.setImageDrawable(getResources().getDrawable(R.drawable.defaultdog));
        String[] defaults={"name","weight","gender","breed","habits","age"};
        ArrayAdapter adapter2=new ArrayAdapter(Profile.this,android.R.layout.simple_list_item_1,defaults);

        mListView.setAdapter(adapter2);
    }
// a method to show the imagepicker
    public void showImagePicker(){
        Toast.makeText(getApplicationContext(),"Please select an Image of your Dog",Toast.LENGTH_LONG).show();
        Intent intent = new Intent();
// Show only images, no videos or anything else
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
// Always show the chooser (if there are multiple options available)
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);

    }
// when an image is picked: the dialog to collect all the information about the dog is shown
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();
            DogPicUri=uri;
            if(iseditopp){
                Dog dog=(mDbAdapter.getDogFromCursor(Activecursor));
            fireCustomDialog(dog);}
            else {
                fireCustomDialog(null);}
        }
        else {Toast.makeText(getApplicationContext(),"Please pick an image",Toast.LENGTH_LONG).show();}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile_options_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.newfile:
                iseditopp=false;
                showImagePicker();
                return true;
            case R.id.viewfile:
                showViewDialog();
                return true;
            default:
                return false;
        }
    }
    //a method to show dialog to collect all the information about the dog is shown and this information plus the uri in string form is inserted into the database
//this method also shows the dialog for editing a dogfile
    private void fireCustomDialog(final Dog dog){

   final Dialog dialog = new Dialog(this);
    dialog.setContentView(R.layout.profile_dialog);
    dialog.setTitle("New Dog");
    final boolean isEditOperation = (dog != null);
    final EditText name,weight,gender,breed,age,habits;
    name=dialog.findViewById(R.id.name_profiledialog);
    weight=dialog.findViewById(R.id.weight_profiledialog);
    gender=dialog.findViewById(R.id.gender_profiledialog);
    breed=dialog.findViewById(R.id.breed_profiledialog);
   age=dialog.findViewById(R.id.age_profiledialog);
    habits=dialog.findViewById(R.id.habits_profiledialog);
    if (isEditOperation){
        name.setText(dog.getDogName());
        weight.setText(dog.getWeight());
       gender.setText(dog.getGender());
        breed.setText(dog.getBreed());
        age.setText(dog.getAge());
        dialog.setTitle("Edit DogFile");
    }


    Button cancel,commit;
    cancel=dialog.findViewById(R.id.profiledialog_cancel);
    commit=dialog.findViewById(R.id.profiledialog_commit);
    commit.setOnClickListener(new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            final String uristring=DogPicUri.toString();
            final String dogname=name.getText().toString();
            final String dogweight= weight.getText().toString();
            final String doggender=gender.getText().toString();
            final String dogbreed= breed.getText().toString();
            final String dogage= age.getText().toString();
            final String doghabits= habits.getText().toString();
            if (isEditOperation) {
               Dog dogEdited = new Dog(dog.getdId(),uristring,dogname,dogweight,doggender,dogbreed,doghabits,dogage);
                mDbAdapter.updateDog(dogEdited);
                updateViews(Uri.parse(uristring),mDbAdapter.fetchDogByName(dogname));
//this is for new reminder
            } else {
                mDbAdapter.createDog(uristring,dogname,dogweight,doggender,dogbreed,doghabits,dogage);
                Toast.makeText(getApplicationContext(),"A dogfile has been successfully added",Toast.LENGTH_LONG).show();
                updateViews(Uri.parse(uristring),mDbAdapter.fetchDogByName(dogname));
            }


            dialog.dismiss();
        }
    });
   cancel.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dialog.dismiss();
        }
    });
dialog.show();

}

// a method to set the listview and imageview to the data of a particular dogfile selected
public void updateViews(Uri uri, Cursor cursor){


    if (uri!=null){
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.MANAGE_DOCUMENTS) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            // Log.d(TAG, String.valueOf(bitmap));
            ImageView imageView = (ImageView) findViewById(R.id.ImageView);
            imageView.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    if(cursor!=null){
        Activecursor=cursor;
        String[] from = new String[]{
                DogsDbAdapter.COL_NAME,DogsDbAdapter.COL_WEIGHT,DogsDbAdapter.COL_GENDER , DogsDbAdapter.COL_BREED, DogsDbAdapter.COL_HABITS, DogsDbAdapter.COL_AGE
        };
        int[] to = new int[]{
                R.id.name_dogdetails,  R.id.weight_dogdetails, R.id.gender_dogdetails, R.id.breed_dogdetails, R.id.habits_dogdetails, R.id.age_dogdetails
        };
      SimpleCursorAdapter  adapter = new SimpleCursorAdapter(
//context
                Profile.this,
//the layout of the row
                R.layout.dogdetailist_layout,
//cursor
                cursor,
//from columns defined in the db
                from,
//to the ids of views in the layout
                to,
//flag - not used
                0);
        mListView.setAdapter(adapter);
    }
}
// a method to show the dialog from which a user can select the dogfile he or she wants to view
public void showViewDialog(){
    final boolean therearedogs;

    AlertDialog.Builder builder = new AlertDialog.Builder(Profile.this);
    ListView modeListView = new ListView(Profile.this);
    final ArrayList<String> dogs = mDbAdapter.fetchDogNames();
    if (dogs.get(0).equals("No Dogs")){
        therearedogs=false;
    }
    else {
        therearedogs=true;
    }
    final ArrayAdapter<String> modeAdapter = new ArrayAdapter<>(Profile.this,
            android.R.layout.simple_list_item_1, android.R.id.text1, dogs);
    modeListView.setAdapter(modeAdapter);
    builder.setView(modeListView);
    final Dialog dialog = builder.create();
    dialog.show();
    modeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//edit reminder
            if(therearedogs){
                String dogname=dogs.get(position);
                Cursor cursor=mDbAdapter.fetchDogByName(dogname);
                updateViews(Uri.parse(cursor.getString(1)),cursor);
            }
            else {
                Toast.makeText(getApplicationContext(),"Please Add a Dog File",Toast.LENGTH_LONG).show();
            }

            dialog.dismiss();
        }
    });


}


}









