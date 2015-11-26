package top.itmp.hostsupdate;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStream;
import java.lang.reflect.MalformedParameterizedTypeException;
import java.net.MalformedURLException;
import java.net.URL;

public class HostUpdate extends AppCompatActivity {

    private static String POSITION = "POSITION";
    final int REQUEST_CODE_ASK_PERMISSIONS = 123;
    private TabLayout tabLayout = null;
    private View rootView = null;
    private TextView hosts_update_tv = null;
    private TextView hosts_delete_tv = null;
    private TextView network_set_tv = null;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hostupdate);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(3);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        int hasWriteStoragePermission = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (hasWriteStoragePermission != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_CODE_ASK_PERMISSIONS);
            // return;
        }

/* Never Used, but left
        switch (tabLayout.getSelectedTabPosition()){
            case 0:
                host_update_tv = (TextView)findViewById(R.id.host_update_tv);
                host_update_btn = (Button)findViewById(R.id.host_update_btn);
                //host_update_tv.setText("hello wordl!!");
                host_update_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        host_update_tv.setText("aaaa");
                    }
                });
                break;
            case 1:
                break;

            case 2:
                break;
            default:
                   break;
        }
*/

/* donot need this for now, comment it.
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
*/
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_hostupdate, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(POSITION, tabLayout.getSelectedTabPosition());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mViewPager.setCurrentItem(savedInstanceState.getInt(POSITION));
    }

    /* for Android M */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode){
            case REQUEST_CODE_ASK_PERMISSIONS:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    //Snackbar.make(null, "Replace with your own action", Snackbar.LENGTH_LONG)
                      //      .setAction("Action", null).show();
                    Toast.makeText(getApplicationContext(), "更新hosts.",Toast.LENGTH_SHORT).show();
                }else {
                   // Snackbar.make(null, "没有授予读写sdcard的权限！\n", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    Toast.makeText(getApplicationContext(), "无法写入sd卡", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    /* add a runAsRoot func to run root shell commond  */
     public String runAsRoot(String cmd, boolean hasOutput) throws Exception {
        Process p = Runtime.getRuntime().exec("su");
        DataOutputStream os = new DataOutputStream(p.getOutputStream());
        InputStream is = p.getInputStream();
        String result = null;
        //for (String tmpCmd : cmds) {
            os.writeBytes(cmd + "\n");
            int readed = 0;
            byte[] buff = new byte[4096];
           // boolean cmdRequiresAnOutput = true;
            if (hasOutput) {
                while (is.available() <= 0) {
                    try {
                        Thread.sleep(1000);
                    } catch (Exception ex) {
                    }
                }

                while (is.available() > 0) {
                    readed = is.read(buff);
                    if (readed <= 0) break;
                    result = new String(buff, 0, readed);
                   // result = seg; //result is a string to show in textview
                }
            }
        //}
        os.writeBytes("exit\n");
        os.flush();
        return result;
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_hostsupdate, container, false);
            //TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            //textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }

    public class TabFragment0 extends Fragment{

        int mCurCheckPosition = 0;
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
           // return super.onCreateView(inflater, container, savedInstanceState);
            rootView = inflater.inflate(R.layout.fragment_hostsupdate, container, false);
            hosts_update_tv = (TextView)rootView.findViewById(R.id.host_update_tv);
            final Button host_update_btn = (Button)rootView.findViewById(R.id.host_update_btn);
            hosts_update_tv.setText("如要更新hosts，确认手机具有root功能\n使用下面的按钮来更新手机hosts，该hosts具有访问Google、facebook 等网站的能力，可以成功使用大部分的Google服务（如：联系人 日历同步， 邮件，Drive等）...");
            host_update_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // host_update_tv.setText("hello world");

                    File file = new File("/system/xbin/su");
                    if (!file.exists()) {
                        hosts_update_tv.setText("手机没有root， 无法进行更新hosts\n");
                        return;
                    }

                    File hosts_file = new File(Environment.getExternalStorageDirectory() + File.separator + "hosts");
                    if(hosts_file.exists())
                        hosts_file.delete();

                    // use downtask to download hosts file to /sdcard
                    DownTask downTask = new DownTask(HostUpdate.this);
                    try {
                        hosts_update_tv.setText("");
                        downTask.execute(new URL("https://raw.githubusercontent.com/racaljk/hosts/master/hosts"));
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }

                            hosts_update_tv.append("下载完成。。\n");
                            try {
                                runAsRoot("mount -o rw,remount /system && mv " + Environment.getExternalStorageDirectory().toString() + File.separator + "hosts" + " /system/etc/hosts && chmod 644 /system/etc/hosts", false);
                                runAsRoot("chown root:root /system/etc/hosts", false);

                                String rtn = runAsRoot("test -e /system/etc/hosts && stat -c \"%n %s\"bytes\"\n" + "%z %U:%G\" /system/etc/hosts || echo -e \"hosts复制失败！\"", true);
                                hosts_update_tv.setText(rtn);
                                hosts_update_tv.append("\n Hosts 更新完成.\n");
                                //String rtn = runAsRoot("mount -o rw,remount /system && mv " + Environment.getExternalStorageDirectory().toString() + File.separator + "hosts" + "/system/etc/hosts && chown root:root /system/etc/hosts && chmod 644 /system/etc/hosts && \"stat -c \\\"%n %s\\\"bytes\\\"\\n%z %U:%G\\\" /system/etc/hosts\\n\"", true);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                }
            });

            return rootView;
        }

        @Override
        public void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
            outState.putInt("curChoice", mCurCheckPosition);
        }

        @Override
        public void onActivityCreated(@Nullable Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            if (savedInstanceState != null) {
                mCurCheckPosition = savedInstanceState.getInt("curChoice", 0);
            }
        }
    }
    public class TabFragment1 extends Fragment{
        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            //return super.onCreateView(inflater, container, savedInstanceState);
            View rootView =  inflater.inflate(R.layout.fragment_hostsdelete, container, false);
            final Button delete_hosts_btn = (Button)rootView.findViewById(R.id.hosts_delete_btn);
            final TextView delete_hosts_tv = (TextView)rootView.findViewById(R.id.hosts_delete_tv);
            delete_hosts_tv.setText("可以使用下面的delete 按钮来删除hosts，同样，该功能需要手机具有root权限。");
            delete_hosts_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    File hosts_file = new File("/system/etc/hosts");
                    if (!hosts_file.exists()) {
                        delete_hosts_tv.setText("hosts文件不存在， 或者已被删除。");
                        return;
                    }
                    try {
                        runAsRoot("mount -o rw,remount /system && rm -rf /system/etc/hosts", false);
                        Thread.sleep(1000);
                        String rtn = runAsRoot("if [ ! -e /system/etc/hosts ]\n" +
                                " then echo -e \"delete /system/etc/hosts success.\n" +
                                "\"\n" +
                                "else echo -e \"delete /system/etc/hosts failed.\"\n" +
                                "fi", true);
                        delete_hosts_tv.setText(rtn);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            return rootView;
        }
    }
    public static class TabFragment2 extends Fragment{
        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            //return super.onCreateView(inflater, container, savedInstanceState);
            return inflater.inflate(R.layout.fragment_networksetting, container, false);
        }
    }
    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            //return PlaceholderFragment.newInstance(position + 1);
            switch(position){
                case 0:
                    TabFragment0 tab0 = new TabFragment0();
                    return tab0;
                case 1:
                    TabFragment1 tab1 = new TabFragment1();
                    return tab1;
                case 2:
                    TabFragment2 tab2 = new TabFragment2();
                    return tab2;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Hosts Update";
                case 1:
                    return "Hosts Delete";
                case 2:
                    return "Network Set";
            }
            return null;
        }
    }
}
