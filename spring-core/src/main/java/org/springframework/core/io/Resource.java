/*
 * Copyright 2002-2018 the original author or authors.
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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import org.springframework.lang.Nullable;

/**
 * 从潜在的资源中抽象出的实际资源类型的描述符接口，比如文件资源或者类资源。
 * Interface for a resource descriptor that abstracts from the actual
 * type of underlying resource, such as a file or class path resource.
 *
 * 任何以物理形式存在的资源都能被转化为输入流，但对于某些资源而言，其只能返回一个
 * URL或文件句柄（？句柄的具体含义）。其具体行为依赖于具体实现。
 * <p>An InputStream can be opened for every resource if it exists in
 * physical form, but a URL or File handle can just be returned for
 * certain resources. The actual behavior is implementation-specific.
 *
 * @author Juergen Hoeller
 * @since 28.12.2003
 * @see #getInputStream()
 * @see #getURL()
 * @see #getURI()
 * @see #getFile()
 * @see WritableResource
 * @see ContextResource
 * @see UrlResource
 * @see FileUrlResource
 * @see FileSystemResource
 * @see ClassPathResource
 * @see ByteArrayResource
 * @see InputStreamResource
 * 作为所有资源的统一抽象，其子类为AbstractResource；对资源的统一定义
 */
public interface Resource extends InputStreamSource {

	/**
	 * 判断对应的资源是否真实存在于物理单元中。
	 * Determine whether this resource actually exists in physical form.
	 * 这个方法执行资源的存在性检查，而资源句柄的存在保证了一个有效的描述符句柄。
	 * <p>This method performs a definitive existence check, whereas the
	 * existence of a {@code Resource} handle only guarantees a valid
	 * descriptor handle.
	 */
	boolean exists();

	/**
	 * 指明该内容非空的资源是否可以通过getInputStream()方法进行读取。
	 * Indicate whether non-empty contents of this resource can be read via
	 * {@link #getInputStream()}.
	 * 对于存在的典型的资源描述符，其总是返回true，因为从严格来说，其完全包含了exists()的语义。
	 * <p>Will be {@code true} for typical resource descriptors that exist
	 * since it strictly implies {@link #exists()} semantics as of 5.1.
	 * 注意，在实际内容读取过程中仍然可能会抛出错误。
	 * Note that actual content reading may still fail when attempted.
	 * 返回值为false时表示这个资源的内容不能够被读取。
	 * However, a value of {@code false} is a definitive indication
	 * that the resource content cannot be read.
	 * @see #getInputStream()
	 * @see #exists()
	 * 判断资源是否可读
	 */
	default boolean isReadable() {
		return exists();
	}

	/**
	 * 指明指定资源是否代表一个已打开的流的句柄。
	 * Indicate whether this resource represents a handle with an open stream.
	 * 如果返回true，则这个输入流不能被多次读取，同时需要在读取后关闭对应的流，以防止资源泄露。
	 * If {@code true}, the InputStream cannot be read multiple times,
	 * and must be read and closed to avoid resource leaks.
	 * 如果是典型的资源描述符，则会返回false。
	 * <p>Will be {@code false} for typical resource descriptors.
	 */
	default boolean isOpen() {
		return false;
	}

	/**
	 * 判断指定支援是否代表文件系统中的一个文件。
	 * Determine whether this resource represents a file in a file system.
	 * 当返回true时，能在一定程度上表示（但不能保证）能成功调用getFile()方法。
	 * A value of {@code true} strongly suggests (but does not guarantee)
	 * that a {@link #getFile()} call will succeed.
	 * 默认的，这个方法返回false。
	 * <p>This is conservatively {@code false} by default.
	 * @since 5.0
	 * @see #getFile()
	 * 判断该资源是否为一个File
	 */
	default boolean isFile() {
		return false;
	}

	/**
	 * 返回指定资源的URL句柄。句柄，简单理解为一个期望目标的临时名称，在URL、URI中表示地址，File中表示文件名。
	 * Return a URL handle for this resource.
	 * 如果一个资源不能被处理为一个URL，则会抛出IOException。
	 * @throws IOException if the resource cannot be resolved as URL,
	 * i.e. if the resource is not available as descriptor
	 * 获取该资源的URL句柄
	 */
	URL getURL() throws IOException;

	/**
	 * 返回指定资源的URI句柄。
	 * Return a URI handle for this resource.
	 * 如果一个资源不能被处理为一个URI，则会抛出IOException。
	 * @throws IOException if the resource cannot be resolved as URI,
	 * i.e. if the resource is not available as descriptor
	 * @since 2.5
	 * 获取该资源的URI句柄
	 */
	URI getURI() throws IOException;

	/**
	 * 返回指定资源的文件句柄。
	 * Return a File handle for this resource.
	 * 如果这个资源不能被处理为一个文件的绝对路径，则会抛出FileNotFoundException。
	 * @throws java.io.FileNotFoundException if the resource cannot be resolved as
	 * absolute file path, i.e. if the resource is not available in a file system
	 * 当读取失败时，抛出IOException。
	 * @throws IOException in case of general resolution/reading failures
	 * @see #getInputStream()
	 */
	File getFile() throws IOException;

	/**
	 * 返回一个可读的字节管道。
	 * Return a {@link ReadableByteChannel}.
	 * 每次调用都会返回一个全新的字节管道流。
	 * <p>It is expected that each call creates a <i>fresh</i> channel.
	 * 默认实现返回一个以getInputStream()方法的结果为基础的Channels对象
	 * <p>The default implementation returns {@link Channels#newChannel(InputStream)}
	 * with the result of {@link #getInputStream()}.
	 * @return the byte channel for the underlying resource (must not be {@code null})
	 * @throws java.io.FileNotFoundException if the underlying resource doesn't exist
	 * @throws IOException if the content channel could not be opened
	 * @since 5.0
	 * @see #getInputStream()
	 * 返回可读字节通道ReadableByteChannel
	 */
	default ReadableByteChannel readableChannel() throws IOException {
		return Channels.newChannel(getInputStream());
	}

	/**
	 * 判断指定资源的长度
	 * Determine the content length for this resource.
	 * @throws IOException if the resource cannot be resolved
	 * (in the file system or as some other known physical resource type)
	 * 获取资源内容长度
	 */
	long contentLength() throws IOException;

	/**
	 * 确定指定资源的最后修改时间戳。
	 * Determine the last-modified timestamp for this resource.
	 * @throws IOException if the resource cannot be resolved
	 * (in the file system or as some other known physical resource type)
	 * 获取资源最后修改时间
	 */
	long lastModified() throws IOException;

	/**
	 * 根据入参，为指定资源进行相应备份。
	 * Create a resource relative to this resource.
	 * @param relativePath the relative path (relative to this resource)
	 * @return the resource handle for the relative resource
	 * @throws IOException if the relative resource cannot be determined
	 * 根据该资源的相对路径创建新资源
	 */
	Resource createRelative(String relativePath) throws IOException;

	/**
	 * 确定文件名称。
	 * Determine a filename for this resource, i.e. typically the last
	 * part of the path: for example, "myfile.txt".
	 * <p>Returns {@code null} if this type of resource does not
	 * have a filename.
	 * 获取该资源的名称
	 */
	@Nullable
	String getFilename();

	/**
	 * 获取资源的描述符。
	 * Return a description for this resource,
	 * to be used for error output when working with the resource.
	 * <p>Implementations are also encouraged to return this value
	 * from their {@code toString} method.
	 * @see Object#toString()
	 * 获取该资源的描述
	 */
	String getDescription();

}
