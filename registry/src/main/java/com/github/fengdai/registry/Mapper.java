package com.github.fengdai.registry;

public interface Mapper<T, K> {

  K map(T model);
}
