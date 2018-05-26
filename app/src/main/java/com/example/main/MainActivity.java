package com.example.main;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.main.adapter.MyAdapter;
import com.example.main.entity.Icon;
import com.example.main.goods.GoodsMsgActivity;
import com.example.main.io_storage.IoInsertActivity;
import com.example.main.io_storage.IoMsgActivity;
import com.example.main.storage_manage.InfoStoActivity;
import com.example.main.yuangong.yuangongMsgActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Context mContext;
    private GridView grid_photo;
    private BaseAdapter mAdapter = null;
    private ArrayList<Icon> mData = null;
    private String role;

    // 轮播图图片资源
    private int[] mImages = {R.mipmap.banner1, R.mipmap.banner2, R.mipmap.banner3, R.mipmap.banner4};
    private List<ImageView> mList;
    Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = MainActivity.this;
        grid_photo = (GridView) findViewById(R.id.grid_photo);
        Intent intent = getIntent();
        role = intent.getStringExtra("role");

        mData = new ArrayList<>();
        mData.add(new Icon(R.mipmap.goodsinfo, "物品信息"));
        mData.add(new Icon(R.mipmap.storeinfo, "仓库信息"));
        if(role.equals("管理员")){
            mData.add(new Icon(R.mipmap.workerinfo, "员工信息"));
        }

        mData.add(new Icon(R.mipmap.ioinfo, "出入库信息"));
        mData.add(new Icon(R.mipmap.iotake, "出入库登记"));
        mData.add(new Icon(R.mipmap.other, "其他"));




        mAdapter = new MyAdapter<Icon>(mData, R.layout.item_grid_icon) {
            @Override
            public void bindView(ViewHolder holder, Icon obj) {
                holder.setImageResource(R.id.img_icon, obj.getiId());
                holder.setText(R.id.txt_icon, obj.getiName());
            }
        };

        grid_photo.setAdapter(mAdapter);

        grid_photo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0: Toast.makeText(mContext, "物品信息", Toast.LENGTH_SHORT).show();
                        Intent i1=new Intent(MainActivity.this, GoodsMsgActivity.class);
                        startActivity(i1);
                        break;
                    case 1:Toast.makeText(mContext, "仓库信息", Toast.LENGTH_SHORT).show();
                        Intent in1=new Intent(MainActivity.this, InfoStoActivity.class);
                        startActivity(in1);
                        break;
                    case 2: Toast.makeText(mContext, "员工信息", Toast.LENGTH_SHORT).show();
                        Intent y=new Intent(MainActivity.this, yuangongMsgActivity.class);
                        startActivity(y);
                        break;
                    case 3:Toast.makeText(mContext, "出入库信息", Toast.LENGTH_SHORT).show();
                        Intent i=new Intent(MainActivity.this, IoMsgActivity.class);
                        startActivity(i);
                        break;
                    case 4: Toast.makeText(mContext, "出入库登记", Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent(MainActivity.this, IoInsertActivity.class);
                        startActivity(intent);
                        break;
                    case 5:Toast.makeText(mContext, "其他", Toast.LENGTH_SHORT).show();
                        break;
                    default:break;
                }

            }
        });
     //轮播图
        final ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        final LinearLayout pointGroup = (LinearLayout) findViewById(R.id.pointgroup);

        // 准备显示的图片集合
        mList = new ArrayList<>();
        for (int i = 0; i < mImages.length; i++) {
            ImageView imageView = new ImageView(this);
            // 将图片设置到ImageView控件上
            imageView.setImageResource(mImages[i]);

            // 将ImageView控件添加到集合
            mList.add(imageView);

            // 制作底部小圆点
            ImageView pointImage = new ImageView(this);
            pointImage.setImageResource(R.drawable.shape_point_selector);

            // 设置小圆点的布局参数
            int PointSize = getResources().getDimensionPixelSize(R.dimen.point_size);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(PointSize, PointSize);

            if (i > 0) {
                params.leftMargin = getResources().getDimensionPixelSize(R.dimen.point_margin);
                pointImage.setSelected(false);
            } else {
                pointImage.setSelected(true);
            }
            pointImage.setLayoutParams(params);
            // 添加到容器里
            pointGroup.addView(pointImage);
        }

        viewPager.setAdapter(new myAdapter());

        // 对ViewPager设置滑动监听
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            int lastPosition;

            @Override
            public void onPageSelected(int position) {
                // 页面被选中

                // 修改position
                position = position % mList.size();

                // 设置当前页面选中
                pointGroup.getChildAt(position).setSelected(true);
                // 设置前一页不选中
                pointGroup.getChildAt(lastPosition).setSelected(false);

                // 替换位置
                lastPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                int currentPosition = viewPager.getCurrentItem();

                if (currentPosition == viewPager.getAdapter().getCount() - 1) {
                    // 最后一页
                    viewPager.setCurrentItem(0);
                } else {
                    viewPager.setCurrentItem(currentPosition + 1);
                }

                mHandler.postDelayed(this, 3000);
            }
        }, 3000);

    }
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("退出")
                .setMessage("是否退出系统?")
                .setNegativeButton("否", null)
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                })
                .show();
    }

    private class myAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            // 返回整数的最大值
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            // return super.instantiateItem(container, position);
            // 修改position
            position = position % mList.size();
            // 将图片控件添加到容器
            container.addView(mList.get(position));

            // 返回
            return mList.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            //super.destroyItem(container, position, object);
            container.removeView((View) object);
        }
    }
}