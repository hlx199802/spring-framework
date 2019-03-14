/*
 * Copyright 2002-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.core.io;

import java.io.IOException;
import java.io.InputStream;

/**
 * 作为输入流源的对象的简单接口（意味着该接口中会产生对应的输入流？）
 * Simple interface for objects that are sources for an {@link InputStream}.
 *
 * 该接口是Spring中更广泛的资源接口的根接口。
 * <p>This is the base interface for Spring's more extensive {@link Resource} interface.
 *
 * 对一次性流而言，InputStreamResource可用于任何给定的InputStream。
 * <p>For single-use streams, {@link InputStreamResource} can be used for any
 * given {@code InputStream}.
 *
 * Spring的ByteArrayResoure或者任何基于文件资源实现都可以被用作具体实例，允许使用者多次读取
 * 底层内容流。
 * Spring's {@link ByteArrayResource} or any
 * file-based {@code Resource} implementation can be used as a concrete
 * instance, allowing one to read the underlying content stream multiple times.
 *
 * 例如，这使得这个接口作为邮件附件的抽象内容源非常有用。
 * This makes this interface useful as an abstract content source for mail
 * attachments, for example.
 *
 * @author Juergen Hoeller
 * @since 20.01.2004
 * @see java.io.InputStream
 * @see Resource
 * @see InputStreamResource
 * @see ByteArrayResource
 */
public interface InputStreamSource {

	/**
	 * 返回底层资源内容的输入流。
	 * Return an {@link InputStream} for the content of an underlying resource.
	 * 其每一次的调用都会创建一个新的输入流。
	 * <p>It is expected that each call creates a <i>fresh</i> stream.
	 * 当你在考虑诸如JavaMail API的时候，这个需求尤为重要，因为其在创建邮件附件的时候需要能够
	 * 多次读取流。
	 * <p>This requirement is particularly important when you consider an API such
	 * as JavaMail, which needs to be able to read the stream multiple times when
	 * creating mail attachments. For such a use case, it is <i>required</i>
	 * that each {@code getInputStream()} call returns a fresh stream.
	 * @return the input stream for the underlying resource (must not be {@code null})
	 * @throws java.io.FileNotFoundException if the underlying resource doesn't exist
	 * @throws IOException if the content stream could not be opened
	 * 获取内容输入流
	 */
	InputStream getInputStream() throws IOException;

}
