/*
 * Copyright © 2018 Apple Inc. and the ServiceTalk project authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.servicetalk.tcp.netty.internal;

import io.servicetalk.transport.api.ServiceTalkSocketOptions;
import io.servicetalk.transport.api.SslConfig;
import io.servicetalk.transport.netty.internal.BuilderUtils;
import io.servicetalk.transport.netty.internal.WireLogInitializer;

import io.netty.handler.logging.LogLevel;

import java.io.InputStream;
import java.net.SocketOption;
import javax.annotation.Nullable;

import static io.servicetalk.transport.netty.internal.SSLContextFactory.forClient;

/**
 * Configuration for TCP based servers. <p>Internal use only.</p>
 */
public final class TcpClientConfig extends ReadOnlyTcpClientConfig {
    /**
     * New instance.
     *
     * @param autoRead If the channels created by this client will have auto-read enabled.
     */
    public TcpClientConfig(boolean autoRead) {
        super(autoRead);
    }

    /**
     * Copy constructor.
     *
     * @param from The original {@link TcpClientConfig} to copy from.
     */
    public TcpClientConfig(TcpClientConfig from) {
        super(from, false);
    }

    /**
     * Enable SSL/TLS using the provided {@link SslConfig}. To disable it pass in {@code null}.
     * @param config the {@link SslConfig}.
     * @return this.
     * @throws IllegalStateException if the {@link SslConfig#getKeyCertChainSupplier()}, {@link SslConfig#getKeySupplier()}, or {@link SslConfig#getTrustCertChainSupplier()}
     * throws when {@link InputStream#close()} is called.
     */
    public TcpClientConfig setSslConfig(@Nullable SslConfig config) {
        if (config != null) {
            sslContext = forClient(config);
            sslHostnameVerificationAlgorithm = config.getHostnameVerificationAlgorithm();
            sslHostnameVerificationHost = config.getHostnameVerificationHost();
            sslHostnameVerificationPort = config.getHostnameVerificationPort();
        } else {
            sslContext = null;
            sslHostnameVerificationAlgorithm = null;
        }
        return this;
    }

    /**
     * Add a {@link SocketOption} for all connections created by this client.
     *
     * @param <T> the type of the value.
     * @param option the option to apply.
     * @param value the value.
     * @return this.
     */
    public <T> TcpClientConfig setOption(SocketOption<T> option, T value) {
        if (option == ServiceTalkSocketOptions.IDLE_TIMEOUT) {
            idleTimeoutMs = (Long) value;
        } else {
            BuilderUtils.addOption(optionMap, option, value);
        }
        return this;
    }

    /**
     * Enables wire-logging for this client at debug level.
     *
     * @param loggerName Name of the logger.
     * @return {@code this}.
     */
    public TcpClientConfig setWireLoggerName(String loggerName) {
        wireLogger = new WireLogInitializer(loggerName, LogLevel.DEBUG);
        return this;
    }

    /**
     * Disabled wire-logging for this client at debug level.
     *
     * @return {@code this}.
     */
    public TcpClientConfig disableWireLog() {
        wireLogger = null;
        return this;
    }

    /**
     * Returns an immutable view of this config, any changes to this config will not alter the returned view.
     *
     * @return {@link ReadOnlyTcpClientConfig}.
     */
    public ReadOnlyTcpClientConfig asReadOnly() {
        return new ReadOnlyTcpClientConfig(this, true);
    }
}
