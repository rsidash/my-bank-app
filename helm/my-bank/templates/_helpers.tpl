{{- define "my-bank.fullname" -}}
{{- .Release.Name }}
{{- end }}

{{- define "my-bank.keycloak.issuerUri" -}}
http://{{ .Release.Name }}-keycloak:{{ .Values.global.keycloak.port }}/realms/{{ .Values.global.keycloak.realm }}
{{- end }}

{{- define "my-bank.keycloak.externalIssuerUri" -}}
{{ .Values.global.keycloak.externalUrl }}/realms/{{ .Values.global.keycloak.realm }}
{{- end }}
