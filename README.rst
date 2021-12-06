Example Spark Streaming Job with Amazon MSK & Protobuf on Databricks
===============================================================

This repository contains an example job which emulates writing/reading events to Kafka with Protobuf SerDe using Spark Streaming.

.. contents:: :local:

Quickstart
----------

* Clone the repository (or open it in Intellij IDEA)
* Generate Protobuf specs via:

.. code-block:: bash

    sbt clean compile

* In Intellij IDEA mark the :code:`target/scala-2.12/src_managed/main` as generated sources root. Important: un-mark nested main/scalapb as generated sources root, otherwise you'll run into issues while compiling the project with Intellij.
* Configure Python environment and Databricks CLI
* Install and configure :code:`dbx`:

.. code-block:: bash

    pip install dbx
    dbx configure --profile-name=<your-databricks-cli-profile-name>

* Provide required properties in the :code:`.env` file:

.. code-block:: bash

    INSTANCE_PROFILE_NAME="your-instance-profile" # instance profile to access the MSK instance
    DATABRICKS_CONFIG_PROFILE="your-databricks-cli-profile-name"
    KAFKA_BOOTSTRAP_SERVERS_TO_SECRETS="" # Kafka Bootstrap Servers string

* Create the secret scope:

.. code-block::

    make create-scope

* Add the secrets:

.. code-block::

    make add-secrets

* Create a new instance pool in your databricks environment with name :code:`dbx-pool`.

* To deploy and launch the job in dev mode (the job won't be created or updated, ephemeral job run will be used):

.. code-block::

    make dev-launch-generator
    make dev-launch-processor

* To deploy the jobs so they'll be reflected in the Jobs UI:

.. code-block::

    make jobs-deploy



Local tests
-----------

Local testing suite requires :code:`sbt` and :code:`Docker`, since we're using :code:`testcontainers` to run Kafka environment for unit tests.

Please find test example in :code:`src/test/scala/net/renarde/dbx/demos/app/UnifiedAppTest.scala`.


