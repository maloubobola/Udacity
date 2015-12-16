package it.jaschke.alexandria.app.books;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import it.jaschke.alexandria.R;
import it.jaschke.alexandria.contract.APIContract;
import it.jaschke.alexandria.contract.DatabaseContract;
import it.jaschke.alexandria.services.BookService;
import it.jaschke.alexandria.services.DownloadImageTask;


public class AddBookFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener {
    private static final String TAG = AddBookFragment.class.getSimpleName();
    private static final int CAMERA_PERMISSION_REQUEST = 42;
    private EditText ean;
    private final int LOADER_ID = 1;
    private View rootView;
    private final String EAN_CONTENT="eanContent";

    public AddBookFragment() {}

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(ean!=null) {
            outState.putString(EAN_CONTENT, ean.getText().toString());
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_add_book, container, false);
        ean = (EditText) rootView.findViewById(R.id.ean);

        ean.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //no need
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //no need
            }

            @Override
            public void afterTextChanged(Editable s) {
                String ean =s.toString();
                //catch isbn10 numbers
                if(ean.length() == 10 && !ean.startsWith("978")){
                    ean = "978" + ean;
                }
                if(ean.length() < 13){
                    clearFields();
                    return;
                }
                //Once we have an ISBN, start a book intent
                Intent bookIntent = new Intent(getActivity(), BookService.class);
                bookIntent.putExtra(APIContract.EAN, ean);
                bookIntent.setAction(APIContract.FETCH_BOOK);
                getActivity().startService(bookIntent);
                AddBookFragment.this.restartLoader();
            }
        });

        rootView.findViewById(R.id.scan_button).setOnClickListener(this);

        rootView.findViewById(R.id.save_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ean.setText("");
            }
        });

        rootView.findViewById(R.id.delete_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent bookIntent = new Intent(getActivity(), BookService.class);
                bookIntent.putExtra(APIContract.EAN, ean.getText().toString());
                bookIntent.setAction(APIContract.DELETE_BOOK);
                getActivity().startService(bookIntent);
                ean.setText("");
            }
        });

        if(savedInstanceState != null){
            ean.setText(savedInstanceState.getString(EAN_CONTENT));
            ean.setHint("");
        }

        return rootView;
    }

    private void restartLoader(){
        getLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        // retrieve scan result
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanningResult != null) {
            String scanContent = scanningResult.getContents();
            EditText ean = (EditText) getActivity().findViewById(R.id.ean);
            ean.setText(scanContent);
        } else {
            Toast.makeText(getActivity(),"No scan data received!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if(ean.getText().length() == 0){
            return null;
        }
        String eanStr= ean.getText().toString();
        if(eanStr.length() == 10 && !eanStr.startsWith("978")){
            eanStr = "978" + eanStr;
        }
        return new CursorLoader(
                getActivity(),
                DatabaseContract.BookEntry.buildFullBookUri(Long.parseLong(eanStr)),
                null,
                null,
                null,
                null
        );
    }

    /**
     * Handle the error when most of the fields are null (test with the isbn 9782754803199 which only contains a title).
     * A book must a least contained a title, subtitle, author and category. Image is optional.
     * If one of the field is missing, display a "Corrupt data error"
     * This also avoid to get a NullPointerException when the "authors" is null and add more consistency to the data.
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (!data.moveToFirst()) {
            return;
        }

        String bookTitle = data.getString(data.getColumnIndex(DatabaseContract.BookEntry.TITLE));
        String bookSubTitle = data.getString(data.getColumnIndex(DatabaseContract.BookEntry.SUBTITLE));
        String authors = data.getString(data.getColumnIndex(DatabaseContract.AuthorEntry.AUTHOR));
        String imgUrl = data.getString(data.getColumnIndex(DatabaseContract.BookEntry.IMAGE_URL));
        String categories = data.getString(data.getColumnIndex(DatabaseContract.CategoryEntry.CATEGORY));

        if(bookTitle == null || bookSubTitle == null || categories == null || authors == null) {
            Toast.makeText(getContext(),getResources().getString(R.string.corrupt_data_error),Toast.LENGTH_LONG).show();
        } else {
            String[] authorsArr = authors.split(",");
            ((TextView) rootView.findViewById(R.id.bookTitle)).setText(bookTitle);
            ((TextView) rootView.findViewById(R.id.bookSubTitle)).setText(bookSubTitle);
            ((TextView) rootView.findViewById(R.id.authors)).setLines(authorsArr.length);
            ((TextView) rootView.findViewById(R.id.authors)).setText(authors.replace(",","\n"));

            if(Patterns.WEB_URL.matcher(imgUrl).matches()){
                new DownloadImageTask((ImageView) rootView.findViewById(R.id.bookCover)).execute(imgUrl);
                rootView.findViewById(R.id.bookCover).setVisibility(View.VISIBLE);
            }

            ((TextView) rootView.findViewById(R.id.categories)).setText(categories);

            rootView.findViewById(R.id.save_button).setVisibility(View.VISIBLE);
            rootView.findViewById(R.id.delete_button).setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {

    }

    private void clearFields(){
        ((TextView) rootView.findViewById(R.id.bookTitle)).setText("");
        ((TextView) rootView.findViewById(R.id.bookSubTitle)).setText("");
        ((TextView) rootView.findViewById(R.id.authors)).setText("");
        ((TextView) rootView.findViewById(R.id.categories)).setText("");
        rootView.findViewById(R.id.bookCover).setVisibility(View.INVISIBLE);
        rootView.findViewById(R.id.save_button).setVisibility(View.INVISIBLE);
        rootView.findViewById(R.id.delete_button).setVisibility(View.INVISIBLE);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        activity.setTitle(R.string.scan);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.scan_button:
                // check Android 6 permission
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    IntentIntegrator.forSupportFragment(this).initiateScan();
                } else {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST);
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case CAMERA_PERMISSION_REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    IntentIntegrator.forSupportFragment(this).initiateScan();
                } else {
                    Toast.makeText(getActivity(),"Access to camera denied. Please enter manually your book.", Toast.LENGTH_SHORT).show();

                }
            }
        }
    }
}
