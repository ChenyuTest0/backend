# docker build --build-arg jar_path=xxx.jar -f docker/Dockerfile . -t {registry_url}:{tag}
# cSpell:ignore amazoncorretto
FROM amazoncorretto:17-al2023

ENV TZ=Asia/Tokyo

COPY service.jar /app/service.jar
RUN chown -R nobody:nobody /app

USER nobody
WORKDIR /app

EXPOSE 8080

ENTRYPOINT [ "java" ]
CMD ["-jar", "/app/service.jar"]
