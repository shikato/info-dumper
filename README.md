# info-dumper
info-dumper is a [Stetho](http://facebook.github.io/stetho/) plugin which show your android application's information.  

![screen gif](http://38.media.tumblr.com/aa7134963258048bfe1758fbaa821111/tumblr_np2dmkXiOC1ro6w1ho1_500.gif)


## Download 

Gradle
``` groovy
repositories {
    maven {
	    url "https://jitpack.io"
	}
} 

dependencies {
    compile 'com.github.shikato:info-dumper:0.0.1'
}
``` 

## Setup 
[Stetho's setup](http://facebook.github.io/stetho/).

Application Class
```java
  @Override
  public void onCreate() {
    super.onCreate();
    
    Stetho.initialize(
        Stetho.newInitializerBuilder(context)
            .enableDumpapp(new MyDumperPluginsProvider(context))
            .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(context))
            .build());
  }

  private static class MyDumperPluginsProvider implements DumperPluginsProvider {
    private final Context mContext;

    public SampleDumperPluginsProvider(Context context) {
      mContext = context;
    }

    @Override
    public Iterable<DumperPlugin> get() {
      ArrayList<DumperPlugin> plugins = new ArrayList<DumperPlugin>();
      for (DumperPlugin defaultPlugin : Stetho.defaultDumperPluginsProvider(mContext).get()) {
        plugins.add(defaultPlugin);
      }
      // Add InfoDumperPlugin
      plugins.add(new InfoDumperPlugin(mContext));
      return plugins;
    }
  }
``` 

## Usage 

### Example
```
dumpapp info buildconf
dumpapp info id
```

### Commands
| Command | Action |
|:-----------|------------:|
| buildconf   |It shows your application's BuildConfig fields.|
| id     | It shows AndroidID, UUID, Advertising ID. |
| dpi       |        It shows dpi info. |
| memory         |  It shows memory info|
| network    |     It shows network info. |
| permission       |  It shows your application's required permissions. |
| lastupdate    |     It shows your application's lastupdate time. |
| error    |     It shows error state info. |
| tel    |     It shows TelephonyManager info. |
| appinfo    |     It shows your application's android.content.pm.ApplicationInfo fields. |
| osbuild    |    It shows your application's android.os.Build fields.|
| all    |     It shows all. |



## License
MIT
