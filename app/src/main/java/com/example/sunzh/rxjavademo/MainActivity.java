package com.example.sunzh.rxjavademo;

import android.content.Intent;
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

import com.example.sunzh.rxjavademo.rest.GitHubService;
import com.example.sunzh.rxjavademo.rest.Repo;
import com.example.sunzh.rxjavademo.rest.RestGenerator;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxAdapterView;
import com.jakewharton.rxbinding.widget.RxCompoundButton;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Observable;
import rx.Observer;
import rx.Scheduler;
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
    private Student[] stus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        generateStus();
//        testRxjava();
//        testTransformation();
//
//        testRxBinding();
//        testRetrofit();
        testRestful();
    }

    private <T extends View> T findview(int id) {
        return (T) findViewById(id);
    }


    /**
     * retrofit测试
     */
    private void testRetrofit() {
        GitHubService service = RestGenerator.generatService();
        Call<List<Repo>> repos = service.listRepos("octocat");
        repos.enqueue(new Callback<List<Repo>>() {
            @Override
            public void onResponse(Call<List<Repo>> call, Response<List<Repo>> response) {
                if (response != null && response.body() != null) {
                    List<Repo> repos1 = response.body();
                    if (repos1.size() >= 0) {
                        Log.e("TAG", "" + repos1.get(0).toString());
                        Toast.makeText(MainActivity.this, repos1.get(0).toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Repo>> call, Throwable t) {

            }
        });
    }

    private void testRxBinding() {
//        RxView
    }

    /**
     * rxjava变换
     * <p>
     * 线程控制器{@link Scheduler}：可以指定每一段代码在什么样的线程中执行
     * RxJava已经为我们提供了几种Scheduler:
     * {@link Schedulers#immediate} 直接在当前线程
     * {@link Schedulers#newThread} 总是新开线程
     * {@link Schedulers#io()} io操作(读写文件、读写数据库、网络信息交互等)所使用的Scheduler.行为模式和 newThread() 差不多，区别在于 io() 的内部实现是是用一个无数量上限的线程池，可以重用空闲的线程，因此多数情况下 io() 比 newThread() 更有效率。不要把计算工作放在 io() 中，可以避免创建不必要的线程。
     * {@link AndroidSchedulers#mainThread} android主线程
     * {@link Schedulers#computation} 计算所使用的线程。通常指的是cpu密集型计算，即不会被io等操作限制性能的操作，例如图形的计算。其使用固定的线程池，大小为cpu核心数，不要把 I/O 操作放在 computation() 中，否则 I/O 操作的等待时间会浪费 CPU。
     *
     * 切换线程：
     * {@link Observable#subscribeOn(rx.Scheduler)()} 指定subscribe() 所发生的线程，即 Observable.OnSubscribe 被激活时所处的线程。或者叫做事件产生的线程。
     * {@link Observable#observeOn(rx.Scheduler)} 指定Subscriber 所运行在的线程。或者叫做事件消费的线程。其指定的是它之后的操作所发生的线程，可以多次调用，随意变换线程
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


//        generateStus();

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
                Log.e("TAG", "当前线程： " + Thread.currentThread().getName());      //消费事件
            }
        };
        Observable.from(stus)
                .subscribeOn(Schedulers.newThread())    //指定：在新的线程中发起
                .observeOn(Schedulers.io())      //指定：在io线程中处理
                .flatMap(new Func1<Student, Observable<String>>() {
                    @Override
                    public Observable<String> call(Student student) {
                        //以下为处理数据
                        String name = Thread.currentThread().getName();
                        Log.e("TAG", "当前线程： " + name);
                        return Observable.from(student.getCourses()); //处理数据
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())      //指定：在主线程中消费事析
                .subscribe(subscriber);


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

    /**
     * 生产数据
     */
    private void generateStus() {
        stus = new Student[3];
        ArrayList<String> course = new ArrayList<>();
        course.add("english");
        course.add("french");
        course.add("chinese");
        ArrayList<String> course1 = new ArrayList<>();
        course1.add("english");
        ArrayList<String> course2 = new ArrayList<>();
        course2.add("english");
        course2.add("chinese");

        stus[0] = new Student("张三", course);
        stus[1] = new Student("李四", course1);
        stus[2] = new Student("王五", course2);
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
        observable.doOnSubscribe(new Action0() {
            @Override
            public void call() {
                Toast.makeText(MainActivity.this, "do on subscribe", Toast.LENGTH_SHORT).show();
//                ProgressBar
            }
        }).doOnNext(new Action1<String>() {
            @Override
            public void call(String s) {
                s = s + "s";
            }
        }).
                subscribe(subscriber);


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
                        testRetrofit();
//                        Toast.makeText(MainActivity.this, "点击后2秒内取一次的触发事件！", Toast.LENGTH_SHORT).show();
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
                startActivity(new Intent(MainActivity.this, SecondActivity.class));
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
    }

    private void testRestful() {
        String API = "https://api.github.com";

//        RestAdapter restAdapter = new RestAdapter.Builder().setLogLevel(RestAdapter.LogLevel.FULL).setEndpoint(API).build();
//        gitapi git = restAdapter.create(gitapi.class);
//        String user = "AuxLuffy";
//        git.getFeed(user, new retrofit.Callback<gitmodel>() {
//            @Override
//            public void success(gitmodel gitmodel, retrofit.client.Response response) {
//                mTv.setText("Github Name: " + gitmodel.getName() + "/nWebsite: " + gitmodel.getBlog()
//                        + "/nCompany Name: " + gitmodel.getCompany());
//            }
//
//            @Override
//            public void failure(RetrofitError error) {
//                mTv.setText(error.getMessage());
//            }
//        });
        final String[] testStr = new String[]{"t1", "t2", "t3"};
        Observable<String> observable = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                subscriber.onStart();
                subscriber.onNext(testStr[0]);
                subscriber.onNext(testStr[1]);
                subscriber.onNext(testStr[2]);
            }
        });
        Subscriber<String> subscriber = new Subscriber<String>() {
            @Override
            public void onStart() {
                super.onStart();
                Log.d("rxjava-suscriber", "onStart");
            }

            @Override
            public void onCompleted() {
                Log.d("rxjava-suscriber", "onCompleted");

            }

            @Override
            public void onError(Throwable e) {
                Log.d("rxjava-suscriber", "onError, msg: " + e.getMessage());

            }

            @Override
            public void onNext(String s) {
                Log.d("rxjava-suscriber", "onNext, param: " + s);

            }


        };

        testCreate();
//        Log.d("rxjava-suscriber", "-------------testCreate---------");
//        observable.subscribe(subscriber);

//        Log.d("rxjava-suscriber", "-------------testJust---------");
//        testJust(observable, subscriber);

//        Log.d("rxjava-suscriber", "-------------testFrom---------");
//        testFrom(testStr, subscriber);

    }

    private void testFrom(String[] testStr, Subscriber<String> subscriber) {
        Observable.from(testStr).subscribe(subscriber);
    }

    private void testJust(Observable<String> observable, Subscriber<String> subscriber) {
        Observable<String> just = Observable.just("a1", "a2", "a3");
        just.subscribe(subscriber);
    }

    /**
     * 测试create创建的observable
     */
    private void testCreate() {
        Observable.create(new Observable.OnSubscribe<Student>() {
            @Override
            public void call(Subscriber<? super Student> subscriber) {
                subscriber.onStart();
                subscriber.onNext(new Student("sunzf", null));
                subscriber.onCompleted();
            }
        }).subscribe(new Observer<Student>() {
            @Override
            public void onCompleted() {
                Log.d("testCreate", "onCompleted");
            }

            @Override
            public void onError(Throwable e) {
                Log.e("testCreate", "onError");
            }

            @Override
            public void onNext(Student student) {
                Log.d("testCreate", student.getName());
            }
        });


        Observable.from(stus)
                .map(new Func1<Student, String>() {
                    @Override
                    public String call(Student student) {
                        return student.getName();
                    }
                })
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        Log.d("action", "name:" + s);
                    }
                });

        Observable.from(stus)
                .flatMap(new Func1<Student, Observable<String>>() {
                    @Override
                    public Observable<String> call(Student student) {
                        return Observable.from(student.getCourses());
                    }
                })
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        Log.d("action", "flatmap:course:" + s);
                    }
                });
    }


}
