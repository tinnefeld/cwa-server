/*
 * Corona-Warn-App
 *
 * SAP SE and all other contributors /
 * copyright owners license this file to you under the Apache
 * License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package app.coronawarn.server.services.distribution.runner;

import app.coronawarn.server.services.distribution.assembly.component.OutputDirectoryProvider;
import app.coronawarn.server.services.distribution.objectstore.ObjectStoreAccess;
import app.coronawarn.server.services.distribution.objectstore.S3Publisher;
import io.minio.errors.MinioException;
import java.io.IOException;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * This runner will sync the base working directory to the S3.
 */
@Component
@Order(3)
public class S3Distribution implements ApplicationRunner {

  private static final Logger logger = LoggerFactory.getLogger(S3Distribution.class);

  @Autowired
  private OutputDirectoryProvider outputDirectoryProvider;

  @Autowired
  private ObjectStoreAccess objectStoreAccess;

  @Override
  public void run(ApplicationArguments args) {
    try {
      Path pathToDistribute = outputDirectoryProvider.getFileOnDisk().toPath().toAbsolutePath();
      S3Publisher s3Publisher = new S3Publisher(pathToDistribute, objectStoreAccess);

      s3Publisher.publish();
      logger.info("Data pushed to CDN successfully.");
    } catch (UnsupportedOperationException | GeneralSecurityException  | MinioException | IOException e) {
      logger.error("Distribution failed.", e);
    }

  }
}
