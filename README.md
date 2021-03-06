# Registry

Registry provides a mechanism for mapping ListView's items to their itemViews. It can generate static code about the item-to-itemView relationship. You'll never care about the item-view-type and view-type-count anymore. They can be counting by Registry automatically.

# How to use

Say, we have a multi-view-type ListView which displays two kind of item: Foo and Bar.

1. Define an annotation named FooBarList with ```@Register``` annotation.
```java
@Register
public @interface FooBarList {
}
```

2. Define ```ViewBinder``` for mapping Foo and Bar to their views.
```java
// For Foo.
@FooBarList // Indicates it's for FooBarList.
@Layout(R.layout.text_view) // Provides layoutRes for Foo's view.
public class FooBinder implements ViewBinder<Foo, TextView> { // R.layout.text_view is a TextView.
  @Override public void bindView(Foo item, TextView view) {
    // Do binding.
  }
}
```
```java
// For Bar.
@FooBarList
@Layout(R.layout.linear_layout)
public class BarBinder implements ViewBinder<Bar, LinearLayout> { // R.layout.linear_layout is a LinearLayout.
  @Override public void bindView(Bar item, LinearLayout view) {
    // Do binding.
  }
}
```

3. Create Registry for FooBarList and define the ListView's Adapter.
```java
public class Adapter extends RegistryAdapter {
  protected Adapter() {
    // Create the Registry.
    super(Registry.create(FooBarList.class));
  }
  // ...
}
```

Done.:tada:

# Download

Gradle:
```groovy
buildscript {
  dependencies {
    classpath 'com.neenbedankt.gradle.plugins:android-apt:1.8'
  }
}

allprojects {
  repositories {
    maven { url "https://oss.sonatype.org/content/repositories/snapshots" }
  }
}
```
```groovy
apply plugin: 'com.neenbedankt.android-apt'

dependencies {
  compile 'com.github.fengdai:registry:1.0.0-SNAPSHOT'
  apt 'com.github.fengdai:registry-compiler:1.0.0-SNAPSHOT'
}
```

# License

    Copyright (C) 2016 Feng Dai

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
