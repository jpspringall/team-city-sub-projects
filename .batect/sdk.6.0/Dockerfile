FROM mcr.microsoft.com/dotnet/sdk:6.0
LABEL org.opencontainers.image.authors="James S"

# Install Dependencies
# RUN apt-get update && apt-get install -y openjdk-11-jdk
# RUN java -version

RUN dotnet tool install --global dotnet-sonarscanner --version 5.8.0

ENV PATH="$PATH:/root/.dotnet/tools"
ENV DOTNET_SONAR_SCANNER_TOOL_VERSION=5.8.0 
ENV DOTNET_6_SDK_VERSION=6.0 
ENV SONAR_SCANNER_MSBUILD_HOME=/opt/sonar-scanner-msbuild 
ENV DOTNET_SKIP_FIRST_TIME_EXPERIENCE=true 
ENV DOTNET_CLI_TELEMETRY_OPTOUT=true

RUN dotnet sonarscanner -h	

CMD ["/bin/bash"]
WORKDIR /code
COPY . .