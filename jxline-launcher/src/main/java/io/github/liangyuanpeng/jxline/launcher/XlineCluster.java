/*
 * Copyright 2016-2021 The jetcd authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.liangyuanpeng.jxline.launcher;

import org.testcontainers.lifecycle.Startable;

import java.net.URI;
import java.util.List;

public interface XlineCluster extends Startable {

    default void restart() {
        stop();
        start();
    }

    String clusterName();

    List<URI> clientEndpoints();

    List<XlineContainer> containers();
}
