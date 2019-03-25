package info.nightscout.androidaps.plugins.general.automation.dialogs;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import info.nightscout.androidaps.R;
import info.nightscout.androidaps.plugins.general.automation.actions.Action;
import info.nightscout.androidaps.plugins.general.automation.triggers.TriggerConnector;

public class EditActionDialog extends DialogFragment {
    private static Action resultAction;

    private Unbinder mUnbinder;
    private Action mAction;

    @BindView(R.id.layout_root)
    LinearLayout mRootLayout;

    @BindView(R.id.viewActionTitle)
    TextView mViewActionTitle;

    public static EditActionDialog newInstance(Action action) {
        Bundle args = new Bundle();
        EditActionDialog fragment = new EditActionDialog();
        fragment.setArguments(args);

        // clone action to static object
        try {
            resultAction = action.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

        return fragment;
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.automation_dialog_action, container, false);
        mUnbinder = ButterKnife.bind(this, view);

        if (savedInstanceState != null) {
            String actionData = savedInstanceState.getString("action");
            if (actionData != null) {
                try {
                    mAction = Action.instantiate(new JSONObject(actionData));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        if (mAction == null)
            mAction = resultAction;

        mViewActionTitle.setText(mAction.friendlyName());
        mRootLayout.removeAllViews();
        mAction.generateDialog(mRootLayout);

        return view;
    }

    @Override
    public void onDestroyView() {
        mUnbinder.unbind();
        super.onDestroyView();
    }

    @OnClick(R.id.ok)
    public void onButtonOk(View view) {
        resultAction.copy(mAction);
        dismiss();
    }

    @OnClick(R.id.cancel)
    public void onButtonCancel(View view) {
        dismiss();
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        bundle.putString("action", mAction.toJSON());
    }
}
