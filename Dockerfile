FROM debian:11

RUN apt-get update \
    && apt-get install -y --no-install-recommends \
        curl \
        gettext \
        git \
        make \
        openjdk-11-jdk \
        python3-pip \
        python3-setuptools \
        unzip \
        zip

COPY requirements.txt /src/
COPY linguaj/requirements.txt /src/linguaj/
RUN pip3 install -r /src/requirements.txt

# Install gradle so that we don't have to do it for each build. Use --help
# because we don't want to run any gradle task for now, the source code is not
# there.
COPY gradle /src/gradle
COPY gradlew /src
RUN /src/gradlew --help

COPY ci/install-android-sdk /src
RUN /src/install-android-sdk

# Must match the value in ci/install-android-sdk
ENV ANDROID_SDK_ROOT=/opt/android-sdk

RUN git config --global --add safe.directory /src/burgerparty

WORKDIR /root
ENTRYPOINT ["/bin/bash"]
