/***
  Copyright (c) 2012-2014 CommonsWare, LLC
  Licensed under the Apache License, Version 2.0 (the "License"); you may not
  use this file except in compliance with the License. You may obtain a copy
  of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
  by applicable law or agreed to in writing, software distributed under the
  License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
  OF ANY KIND, either express or implied. See the License for the specific
  language governing permissions and limitations under the License.
  
  Covered in detail in the book _The Busy Coder's Guide to Android Development_
    https://commonsware.com/Android
 */

package com.commonsware.android.calllog.consumer;

import android.Manifest;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.CallLog;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class CallLogConsumerActivity extends AbstractPermissionActivity implements
    LoaderManager.LoaderCallbacks<Cursor>,
    SimpleCursorAdapter.ViewBinder {
  private static final String[] PERMS={Manifest.permission.READ_CALL_LOG};
  private static final String[] PROJECTION=new String[] {
      CallLog.Calls._ID, CallLog.Calls.NUMBER, CallLog.Calls.DATE };
  private SimpleCursorAdapter adapter=null;

  @Override
  protected String[] getDesiredPermissions() {
    return(PERMS);
  }

  @Override
  protected void onPermissionDenied() {
    Toast
      .makeText(this, R.string.msg_no_perm, Toast.LENGTH_LONG)
      .show();
    finish();
  }

  @Override
  public void onReady() {
    adapter=
        new SimpleCursorAdapter(this, R.layout.row, null, new String[] {
            CallLog.Calls.NUMBER, CallLog.Calls.DATE }, new int[] {
            R.id.number, R.id.date }, 0);

    adapter.setViewBinder(this);
    setListAdapter(adapter);
    getLoaderManager().initLoader(0, null, this);
  }

  @Override
  public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {
    return(new CursorLoader(this, CallLog.Calls.CONTENT_URI,
                            PROJECTION, null, null, CallLog.Calls.DATE
                                + " DESC"));
  }

  @Override
  public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
    adapter.swapCursor(cursor);
  }

  @Override
  public void onLoaderReset(Loader<Cursor> loader) {
    adapter.swapCursor(null);
  }

  @Override
  public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
    if (columnIndex==2) {
      long time=cursor.getLong(columnIndex);
      String formattedTime=DateUtils.formatDateTime(this, time,
                              DateUtils.FORMAT_ABBREV_RELATIVE);

      ((TextView)view).setText(formattedTime);

      return(true);
    }

    return(false);
  }
}