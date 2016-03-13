# info-dumper
Info-dumper is a [Stetho](http://facebook.github.io/stetho/) plugin to show your android application's information.  

![screen gif](http://38.media.tumblr.com/aa7134963258048bfe1758fbaa821111/tumblr_np2dmkXiOC1ro6w1ho1_500.gif)


## Download 

**build.gradle**
``` groovy
repositories {
    maven {
        url 'http://dl.bintray.com/shikato/maven'
    }
} 

dependencies {
    compile 'org.shikato.infodumper:info-dumper:0.0.3'
}
``` 

## Setup 
[Stetho's setup](http://facebook.github.io/stetho/).

**Your Application Class**
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

    public MyDumperPluginsProvider(Context context) {
      mContext = context;
    }

    @Override
    public Iterable<DumperPlugin> get() {
      List<DumperPlugin> plugins = new ArrayList<>();
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
dumpapp info dpi
dumpapp info buildconf
```

### Commands
| Command | Action |
|:-----------|------------:|
| buildconf   |BuildConfig fields|
| id     | AndroidID, UUID, Advertising ID|
| dpi       |        dpi info|
| memory         |  Memory info|
| network    |     Network info|
| permission       |  Required permissions|
| lastupdate    |     Lastupdate time|
| error    |     Error state info|
| tel    |     TelephonyManager info|
| appinfo    |     android.content.pm.ApplicationInfo fields|
| osbuild    |    android.os.Build fields|
| all    |     all |



## License
MIT
