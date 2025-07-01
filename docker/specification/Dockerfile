FROM nginx:1.28

ENV TZ=Asia/Tokyo

COPY index.html /usr/share/nginx/html
COPY docs /usr/share/nginx/html/e2e
COPY default.conf /etc/nginx/conf.d/default.conf

RUN touch /var/run/nginx.pid && \
  chown -R nginx:nginx /var/run/nginx.pid /run/nginx.pid
USER nginx

ENTRYPOINT ["nginx", "-g", "daemon off;"]
