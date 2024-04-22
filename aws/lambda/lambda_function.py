import json
import boto3

def lambda_handler(event, context):
    glue_client = boto3.client('glue')
    
    job_name = 'csci5409_etl_job'
    
    try:
        response = glue_client.start_job_run(JobName=job_name)
        
        print('Glue job started:', response['JobRunId'])
        
        return {
            'statusCode': 200,
            'body': {
                'message': 'Glue job started successfully',
                'jobId': response['JobRunId']
            }
        }
    except Exception as e:
        print('Error triggering Glue job:', e)
        return {
            'statusCode': 500,
            'body': 'Error triggering Glue job'
        }
