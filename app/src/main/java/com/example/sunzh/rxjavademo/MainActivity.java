package com.example.sunzh.rxjavademo;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxAdapterView;
import com.jakewharton.rxbinding.widget.RxCompoundButton;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Action4;
import rx.functions.Func1;
import rx.schedulers.Schedulers;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    /**
     * Hello World!
     */
    private TextView mTv;
    private ListView mListview;
    /**
     * Login
     */
    private Button mButton;
    private CheckBox mCheckbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
//        testRxjava();
        testTransformation();

        testRxBinding();
    }

    private void testRxBinding() {
//        RxView
    }

    /**
     * 变换
     */
    private void testTransformation() {
//        Observable.just("images/logo.png")
//                .map(new Func1<String, Bitmap>() {
//                    @Override
//                    public Bitmap call(String filePath) {
//                        return getBitmapFromPath(filePath);
//                    }
//                })
//                .subscribe(new Action1<Bitmap>() {
//                    @Override
//                    public void call(Bitmap bitmap) {
//                        showBitmap(bitmap);
//                    }
//                });


        Student[] stus = new Student[3];
        ArrayList<String> course = new ArrayList<>();
        course.add("english");
        course.add("french");
        course.add("chinese");
        ArrayList<String> course1 = new ArrayList<>();
        course1.add("english");
        ArrayList<String> course2 = new ArrayList<>();
        course2.add("english");
        course2.add("chinese");

        stus[0] = new Student("李三", course);
        stus[1] = new Student("李三", course1);
        stus[2] = new Student("李三", course2);

        Subscriber<String> subscriber = new Subscriber<String>() {
            String tag = "course";

            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(String s) {
                Log.d(tag, s);
            }
        };
        Observable.from(stus)
                .subscribeOn(Schedulers.io())//后台线程取数据
                .observeOn(AndroidSchedulers.mainThread())//主线程显示
                .flatMap(new Func1<Student, Observable<String>>() {
                    @Override
                    public Observable<String> call(Student student) {
                        return Observable.from(student.getCourses());
                    }
                }).subscribe(subscriber);


        final Observable<String> observable = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                subscriber.onNext("1");
                subscriber.onNext("2");
                subscriber.onNext("3");
                subscriber.onCompleted();
            }
        });

        observable.lift(new Observable.Operator<Integer, String>() {
            @Override
            public Subscriber<? super String> call(final Subscriber<? super Integer> subscriber) {
                return new Subscriber<String>() {
                    @Override
                    public void onCompleted() {
                        subscriber.onCompleted();
                    }

                    @Override
                    public void onError(Throwable e) {
                        subscriber.onError(e);
                    }

                    @Override
                    public void onNext(String s) {
                        subscriber.onNext(Integer.parseInt(s));
                    }
                };
            }
        });


    }

    private void showBitmap(Bitmap bitmap) {

    }

    private Bitmap getBitmapFromPath(String filePath) {
        File file = new File(filePath);
        boolean exists = file.exists();
        if (!exists) {
            file.mkdirs();
        }
        Toast.makeText(MainActivity.this, "filePath: " + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
        return null;
    }

    /**
     * rxjava的基础用法
     */
    private void testRxjava() {
        Observer<String> observer = new Observer<String>() {

            private String tag = "Observer";

            @Override
            public void onNext(String s) {
                Log.d(tag, "onNext()");
            }

            @Override
            public void onCompleted() {
                Log.d(tag, "onComplete()");
            }

            @Override
            public void onError(Throwable e) {
                Log.d(tag, "onError()");

            }
        };

        Subscriber<String> subscriber = new Subscriber<String>() {
            private String tag = "Subscriber";

            //在subscribe刚开发且事件未发送之前执行，此方法总是在subscribe所发生的线程被调用，
            // 不能指定线程，要在指定线程 来做准 备工作，可以使用doOnSubscribe()方法
            @Override
            public void onStart() {
                super.onStart();
                Log.d(tag, "onStart()");
            }

            @Override
            public void onNext(String s) {
                Log.d(tag, "onNext()" + s);
            }

            @Override
            public void onCompleted() {
                Log.d(tag, "onComplete()");
            }

            @Override
            public void onError(Throwable e) {
                Log.d(tag, "onError()");
            }
        };

        Observable<String> observable = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                subscriber.onNext("1");
                subscriber.onNext("2");
                subscriber.onNext("3");
                subscriber.onCompleted();
            }
        });
        observable.subscribe(subscriber);


        Observable<String> observable1 = Observable.just("hello", "hi", "Aloha");


        Observable<String> observable2 = Observable.from(new String[]{"hello", "hi", "Aloha"});

        //不完整定义的回调
        Action1<String> onNextAction = new Action1<String>() {

            public String tag = "Action1";

            @Override
            public void call(String s) {
                Log.d(tag, s);
            }
        };
        Action1<Throwable> onErrorAction = new Action1<Throwable>() {

            public String tag = "Action1";

            @Override
            public void call(Throwable throwable) {
                Log.d(tag, "onErrorAction");
            }
        };
        Action0 onCompletedAction = new Action0() {
            public String tag = "Action0";

            @Override
            public void call() {
                Log.d(tag, "onCompletedAction");
            }
        };
        observable.subscribe(onNextAction);
        observable1.subscribe(onNextAction, onErrorAction);
        observable2.subscribe(onNextAction, onErrorAction, onCompletedAction);

        new Action4<Short, String, Boolean, Boolean>() {

            @Override
            public void call(Short aShort, String s, Boolean aBoolean, Boolean aBoolean2) {

            }
        };

    }

    private void initView() {
        textRxBinding();
    }

    /**
     * RxBinding测试
     */
    private void textRxBinding() {
        mTv = (TextView) findViewById(R.id.tv);
//        mTv.setOnClickListener(this);

        RxView.clicks(mTv)
                .throttleFirst(2, TimeUnit.SECONDS)//两秒内只取一次点击事件，防抖动操作
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        Toast.makeText(MainActivity.this, "点击后2秒内取一次的触发事件！", Toast.LENGTH_SHORT).show();
                    }
                });
        mListview = (ListView) findViewById(R.id.listview);
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < 40; i++) {
            list.add("sss" + i);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_expandable_list_item_1);
        adapter.addAll(list);

        mListview.setAdapter(adapter);

        RxAdapterView.itemClicks(mListview).subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                Toast.makeText(MainActivity.this, "item click" + integer, Toast.LENGTH_SHORT).show();
            }
        });

        RxAdapterView.itemLongClicks(mListview).subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                Toast.makeText(MainActivity.this, "item long click" + integer, Toast.LENGTH_SHORT).show();
            }
        });
        mButton = (Button) findViewById(R.id.button);
        mCheckbox = (CheckBox) findViewById(R.id.checkbox);

        RxCompoundButton.checkedChanges(mCheckbox).subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean aBoolean) {
                mButton.setEnabled(aBoolean);
                mButton.setBackgroundResource(aBoolean ? R.color.button_yes : R.color.button_no);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv:
                break;
        }
    }
}
