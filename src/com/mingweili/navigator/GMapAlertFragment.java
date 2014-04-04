package com.mingweili.navigator;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;

/**
 * Fragment of alert dialog shown before navigation starts. 
 */
public class GMapAlertFragment extends DialogFragment {
	
	/**
	 * Interface for invoker (Navigation activity) to implement to respond to confirm button click event
	 */
	public interface NoticeDialogListener {
        public void onDialogPositiveClick(boolean notShowChecked);
    }
	
	private NoticeDialogListener mListener;			// Handle of invoker of this dialog to inject confirm button click event listener
	
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    // Get the layout inflater
	    LayoutInflater inflater = getActivity().getLayoutInflater();

	    // Inflate and set the layout for the dialog
	    // Pass null as the parent view because its going in the dialog layout
	    final View view = inflater.inflate(R.layout.fragment_gmap_alert, null);
	    builder.setView(view)
	    // Add action buttons
	           .setPositiveButton(R.string.alert_confirm_label, new DialogInterface.OnClickListener() {
	               @Override
	               public void onClick(DialogInterface dialog, int id) {
	            	   CheckBox notShowCheckbox 
	            	   		= (CheckBox)view.findViewById(R.id.alert_not_show_again_checkbox);
	            	   boolean isChecked = notShowCheckbox.isChecked();
	            	   
	            	   GMapAlertFragment.this.mListener.onDialogPositiveClick(isChecked);
	               }
	           })
	           .setNegativeButton(R.string.alert_cancel_label, new DialogInterface.OnClickListener() {
	               public void onClick(DialogInterface dialog, int id) {
	            	   GMapAlertFragment.this.getDialog().cancel();
	               }
	           });   
	    return builder.create();
    }
	
	// Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (NoticeDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }
}
