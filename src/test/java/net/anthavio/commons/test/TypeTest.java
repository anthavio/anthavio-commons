/**
 * 
 */
package net.anthavio.commons.test;

import static org.fest.assertions.api.Assertions.assertThat;

import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

import net.anthavio.util.ReflectUtil;

import org.testng.annotations.Test;

/**
 * Type genericSuperclass = getClass().getGenericSuperclass(); Type first =
 * ((ParameterizedType) genericSuperclass).getActualTypeArguments()[0];
 * 
 * lze pouzit pouze pokud se predpoklada primy potomek teto genericke tridy s
 * zafixovanym generikem
 * 
 * @author vanek
 */
public class TypeTest {

	@Test
	public void testSubclass() {

		Class<?> xt = ReflectUtil.getTypeArguments(GC.class, new GC().getClass()).get(0);
		assertThat(xt).isNull(); // compile type erasure

		Class<?> xts = ReflectUtil.getTypeArguments(GC.class, new GC<String>().getClass()).get(0);
		assertThat(xts).isNull(); // compile type erasure

		Class<?> yt = ReflectUtil.getTypeArguments(GC.class, new GCS().getClass()).get(0);
		assertThat(yt).isNull(); // compile type erasure

		Class<?> yts = ReflectUtil.getTypeArguments(GC.class, new GCS<String>().getClass()).get(0);
		assertThat(yts).isNull(); // compile type erasure

		Type[] types = ReflectUtil.getActualTypeArguments(new GCS<String>().getClass(), GC.class);
		System.out.println(types[0].getClass());

		// Only when generic type is specified in class declaration

		Class<String> yst = (Class<String>) ReflectUtil.getTypeArguments(GC.class, new CC().getClass()).get(0);
		assertThat(yst).isEqualTo(String.class);

		Type[] yst2 = ReflectUtil.getActualTypeArguments(new CC().getClass(), GC.class);
		assertThat(yst2[0]).isEqualTo(String.class);

		Class<String> zt = (Class<String>) ReflectUtil.getTypeArguments(GC.class, new CSC().getClass()).get(0);
		assertThat(zt).isEqualTo(String.class);

		Type[] zt2 = ReflectUtil.getActualTypeArguments(new CC().getClass(), GC.class);
		assertThat(zt2[0]).isEqualTo(String.class);

	}

	class GC<T> {

	}

	class GCS<T> extends GC<T> {

	}

	class CC extends GC<String> {

	}

	class CSC extends GCS<String> {

	}

	@Test
	public void testInterface() {

		Class<?> at = ReflectUtil.getTypeArguments(A.class, new A<String>().getClass()).get(0);
		assertThat(at).isNull(); // compile type erasure

		Type[] at2 = ReflectUtil.getActualTypeArguments(new A<String>().getClass(), I.class);
		System.out.println(((TypeVariable<GenericDeclaration>) at2[0]).getGenericDeclaration());

		Class<?> bt = ReflectUtil.getTypeArguments(A.class, new B<String>().getClass()).get(0);
		assertThat(bt).isNull(); // compile type erasure

		Class<String> ct = (Class<String>) ReflectUtil.getTypeArguments(A.class, new C().getClass()).get(0);
		assertThat(ct).isEqualTo(String.class);

		Type[] ct2 = ReflectUtil.getActualTypeArguments(new C().getClass(), A.class);
		assertThat(ct2[0]).isEqualTo(String.class);

		// Class<?> dt = TypeUtil.getTypeArguments(I.class, new
		// D().getClass()).get(0);
		// assertThat(dt).isEqualTo(String.class);
		// upadne na nullpointerexception

		// toto umi genericke interface
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
