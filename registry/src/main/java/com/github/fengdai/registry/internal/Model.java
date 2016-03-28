package com.github.fengdai.registry.internal;

import android.view.View;
import com.github.fengdai.registry.Mapper;
import com.github.fengdai.registry.Registry;
import com.github.fengdai.registry.ViewBinder;
import com.github.fengdai.registry.ViewProvider;
import java.util.LinkedHashMap;
import java.util.Map;

public abstract class Model<T> {
  protected final Class<T> modelClass;

  Model(Class<T> modelClass) {
    this.modelClass = modelClass;
  }

  Class<T> getModelClass() {
    return modelClass;
  }

  abstract Registry.ItemView<T, ?> getItemView(T item);

  public static <T> ToOneBuilder<T> oneToOne(Class<T> modelClass) {
    return new ToOneBuilder<>(modelClass);
  }

  public static <T, K> ToManyBuilder<T, K> oneToMany(Class<T> modelClass,
      Class<? extends Mapper<T, K>> mapperClass) {
    return new ToManyBuilder<>(modelClass, mapperClass);
  }

  public static class ToOneBuilder<T> {
    private final Class<T> modelClass;
    private Registry.ItemView<T, ?> itemView;

    public ToOneBuilder(Class<T> modelClass) {
      this.modelClass = modelClass;
    }

    public <V extends View> ToOneBuilder<T> add(int itemViewType,
        Class<? extends ViewBinder<T, V>> viewBinderClass, final int layoutRes) {
      this.itemView = new IvForLayoutRes<>(modelClass, itemViewType, viewBinderClass, layoutRes);
      return this;
    }

    public <BV extends View, PV extends BV> ToOneBuilder<T> add(int itemViewType,
        Class<? extends ViewBinder<T, BV>> viewBinderClass,
        Class<? extends ViewProvider<PV>> viewProviderClass) {
      this.itemView =
          new IvForViewProvider<>(modelClass, itemViewType, viewBinderClass, viewProviderClass);
      return this;
    }

    public Model<T> build() {
      return new ModelToOne<>(modelClass, itemView);
    }
  }

  public static class ToManyBuilder<T, K> {
    private final Class<T> modelClass;
    private final Class<? extends Mapper<T, K>> mapperClass;
    private Map<K, Registry.ItemView<T, ?>> itemViewMap = new LinkedHashMap<>();

    private ToManyBuilder(Class<T> modelClass, Class<? extends Mapper<T, K>> mapperClass) {
      this.modelClass = modelClass;
      this.mapperClass = mapperClass;
    }

    public <V extends View> ToManyBuilder<T, K> add(K key, int itemViewType,
        Class<? extends ViewBinder<T, V>> viewBinderClass, final int layoutRes) {
      itemViewMap.put(key,
          new IvForLayoutRes<>(modelClass, itemViewType, viewBinderClass, layoutRes));
      return this;
    }

    public <BV extends View, PV extends BV> ToManyBuilder<T, K> add(K key, int itemViewType,
        Class<? extends ViewBinder<T, BV>> viewBinderClass,
        Class<? extends ViewProvider<PV>> viewProviderClass) {
      itemViewMap.put(key,
          new IvForViewProvider<>(modelClass, itemViewType, viewBinderClass, viewProviderClass));
      return this;
    }

    public Model<T> build() {
      return new ModelToMany<>(modelClass, mapperClass, itemViewMap);
    }
  }
}
