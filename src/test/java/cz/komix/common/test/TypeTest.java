/**
 * 
 */
package cz.komix.common.test;

import static org.fest.assertions.Assertions.assertThat;

import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

import org.testng.annotations.Test;

import cz.komix.util.ReflectUtil;

/**
 * Type genericSuperclass = getClass().getGenericSuperclass();
 * Type first = ((ParameterizedType) genericSuperclass).getActualTypeArguments()[0];
 * 
 * lze pouzit pouze pokud se predpoklada primy potomek teto genericke tridy s zafixovanym generikem
 * 
 * @author vanek
 */
public class TypeTest {

	@Test
	public void testSubclass() {

		Class<?> xt = ReflectUtil.getTypeArguments(X.class, new X().getClass()).get(0);
		assertThat(xt).isNull(); //compile type erasure

		Class<?> xts = ReflectUtil.getTypeArguments(X.class, new X<String>().getClass()).get(0);
		assertThat(xts).isNull(); //compile type erasure

		Class<?> yt = ReflectUtil.getTypeArguments(X.class, new Y().getClass()).get(0);
		assertThat(yt).isNull(); //compile type erasure

		Class<?> yts = ReflectUtil.getTypeArguments(X.class, new Y<String>().getClass()).get(0);
		assertThat(yts).isNull(); //compile type erasure

		Type[] types = ReflectUtil.getActualTypeArguments(new Y<String>().getClass(), X.class);
		System.out.println(types[0].getClass());

		//Only when generic type is specified in class declaration

		Class<?> yst = ReflectUtil.getTypeArguments(X.class, new YS().getClass()).get(0);
		assertThat(yst).isEqualTo(String.class);

		Type[] yst2 = ReflectUtil.getActualTypeArguments(new YS().getClass(), X.class);
		assertThat(yst2[0]).isEqualTo(String.class);

		Class<?> zt = ReflectUtil.getTypeArguments(X.class, new Z().getClass()).get(0);
		assertThat(zt).isEqualTo(String.class);

		Type[] zt2 = ReflectUtil.getActualTypeArguments(new YS().getClass(), X.class);
		assertThat(zt2[0]).isEqualTo(String.class);

	}

	class X<T> {

	}

	class Y<T> extends X<T> {

	}

	class YS extends X<String> {

	}

	class Z extends Y<String> {

	}

	@Test
	public void testInterface() {

		Class<?> at = ReflectUtil.getTypeArguments(A.class, new A<String>().getClass()).get(0);
		assertThat(at).isNull(); //compile type erasure

		Type[] at2 = ReflectUtil.getActualTypeArguments(new A<String>().getClass(), I.class);
		System.out.println(((TypeVariable<GenericDeclaration>)at2[0]).getGenericDeclaration());

		Class<?> bt = ReflectUtil.getTypeArguments(A.class, new B<String>().getClass()).get(0);
		assertThat(bt).isNull(); //compile type erasure

		Class<?> ct = ReflectUtil.getTypeArguments(A.class, new C().getClass()).get(0);
		assertThat(ct).isEqualTo(String.class);

		Type[] ct2 = ReflectUtil.getActualTypeArguments(new C().getClass(), A.class);
		assertThat(ct2[0]).isEqualTo(String.class);

		//Class<?> dt = TypeUtil.getTypeArguments(I.class, new D().getClass()).get(0);
		//assertThat(dt).isEqualTo(String.class);
		//upadne na nullpointerexception

		//toto umi genericke interface
		Type[] dt2 = ReflectUtil.getActualTypeArguments(new D().getClass(), I.class);
		assertThat(dt2[0]).isEqualTo(String.class);
	}

	interface I<T> {

	}

	class A<T> implements I<T> {

	}

	class B<T> extends A<T> {

	}

	class C extends A<String> {

	}

	class D implements I<String> {

	}

}
