package com.github.fengdai.registry.compiler;

import com.github.fengdai.registry.internal.Model;
import com.github.fengdai.registry.internal.RegistryImpl;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.WildcardTypeName;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

final class RegistryClass {
  private static final ClassName REGISTRY_IMPL = ClassName.get(RegistryImpl.class);

  private final String classPackage;
  private final String className;
  private final int viewTypeCount;
  private final List<Binding> bindings = new LinkedList<>();

  RegistryClass(String classPackage, String className, int viewTypeCount) {
    this.classPackage = classPackage;
    this.className = className;
    this.viewTypeCount = viewTypeCount;
  }

  void addBinding(Binding binding) {
    bindings.add(binding);
  }

  JavaFile brewJava() {
    TypeSpec.Builder result =
        TypeSpec.classBuilder(className).addModifiers(Modifier.PUBLIC).superclass(REGISTRY_IMPL);
    result.addMethod(constructor());
    result.addMethod(createModelsMethod());
    addCreateModelMethods(result);
    return JavaFile.builder(classPackage, result.build())
        .addFileComment("Generated code from Registry. Do not modify!")
        .build();
  }

  private void addCreateModelMethods(TypeSpec.Builder result) {
    for (Binding binding : bindings) {
      MethodSpec method;
      if (binding instanceof ToOneBinding) {
        method = createToOneModelMethod((ToOneBinding) binding);
      } else {
        method = createToManyModelMethod((ToManyBinding) binding);
      }
      result.addMethod(method);
    }
  }

  private MethodSpec constructor() {
    return MethodSpec.constructorBuilder()
        .addModifiers(Modifier.PUBLIC)
        .addStatement("super(createModels(), $L)", viewTypeCount)
        .build();
  }

  private MethodSpec createToOneModelMethod(ToOneBinding binding) {
    MethodSpec.Builder result = buildCreateModelMethod(binding);
    CodeBlock.Builder codeBlockBuilder = CodeBlock.builder()
        .add("return Model.oneToOne($T.class)\n", ClassName.get(binding.getModelType()));
    ItemViewClass itemViewClass = binding.getItemViewClass();
    if (itemViewClass.isViewLayoutRes()) {
      codeBlockBuilder.add("    .add($L, $T.class, $L)\n", itemViewClass.getType(),
          ClassName.get(itemViewClass.getBinderType()), itemViewClass.getLayoutRes());
    } else {
      codeBlockBuilder.add("    .add($L, $T.class, $T.class)\n", itemViewClass.getType(),
          ClassName.get(itemViewClass.getBinderType()),
          ClassName.get(itemViewClass.getViewProviderType()));
    }
    codeBlockBuilder.add("    .build();\n");
    result.addCode(codeBlockBuilder.build());
    return result.build();
  }

  private MethodSpec createToManyModelMethod(ToManyBinding binding) {
    MethodSpec.Builder result = buildCreateModelMethod(binding);
    CodeBlock.Builder codeBlockBuilder = CodeBlock.builder()
        .add("return Model.oneToMany($T.class, $T.class)\n", ClassName.get(binding.getModelType()),
            ClassName.get(binding.getMapperType()));
    Set<Map.Entry<Object, ItemViewClass>> entrySet = binding.getItemViewClassMap().entrySet();
    for (Map.Entry<Object, ItemViewClass> entry : entrySet) {
      Object key = entry.getKey();
      ItemViewClass itemViewClass = entry.getValue();
      if (itemViewClass.isViewLayoutRes()) {
        codeBlockBuilder.add("    .add($T.class, $L, $T.class, $L)\n",
            ClassName.get((TypeElement) key), itemViewClass.getType(),
            ClassName.get(itemViewClass.getBinderType()), itemViewClass.getLayoutRes());
      } else {
        codeBlockBuilder.add("    .add($T.class, $L, $T.class, $T.class)\n",
            ClassName.get((TypeElement) key), itemViewClass.getType(),
            ClassName.get(itemViewClass.getBinderType()),
            ClassName.get(itemViewClass.getViewProviderType()));
      }
    }
    codeBlockBuilder.add("    .build();\n");
    result.addCode(codeBlockBuilder.build());
    return result.build();
  }

  private static MethodSpec.Builder buildCreateModelMethod(Binding binding) {
    return MethodSpec.methodBuilder(createModelMethodName(binding))
        .addModifiers(Modifier.PRIVATE, Modifier.STATIC)
        .returns(ParameterizedTypeName.get(ClassName.get(Model.class),
            ClassName.get(binding.getModelType())));
  }

  private MethodSpec createModelsMethod() {
    MethodSpec.Builder result =
        MethodSpec.methodBuilder("createModels").addModifiers(Modifier.PRIVATE, Modifier.STATIC).
            returns(ParameterizedTypeName.get(ClassName.get(Map.class),
                ParameterizedTypeName.get(ClassName.get(Class.class),
                    WildcardTypeName.subtypeOf(TypeName.OBJECT)),
                ParameterizedTypeName.get(ClassName.get(Model.class),
                    WildcardTypeName.subtypeOf(TypeName.OBJECT))));
    result.addStatement("Map<Class<?>, Model<?>> map = new $T<>()", LinkedHashMap.class);
    for (Binding binding : bindings) {
      result.addStatement("map.put($T.class, $L())", ClassName.get(binding.getModelType()),
          createModelMethodName(binding));
    }
    result.addStatement("return map");
    return result.build();
  }

  private static String createModelMethodName(Binding binding) {
    return binding.getModelType().getQualifiedName().toString().replace('.', '_');
  }
}
