import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import ru.netology.patient.entity.BloodPressure;
import ru.netology.patient.entity.HealthInfo;
import ru.netology.patient.entity.PatientInfo;
import ru.netology.patient.repository.PatientInfoFileRepository;
import ru.netology.patient.service.alert.SendAlertService;
import ru.netology.patient.service.alert.SendAlertServiceImpl;
import ru.netology.patient.service.medical.MedicalService;
import ru.netology.patient.service.medical.MedicalServiceImpl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

class MedicalServiceImplTest {

    @Test
    void checkBloodPressure() {
        PatientInfo patientInfo = new PatientInfo(UUID.randomUUID().toString(),
                "Иван", "Иванов", LocalDate.of(1969, 12, 31),
                new HealthInfo(new BigDecimal("36.6"), new BloodPressure(120, 80)));

        PatientInfoFileRepository patientInfoFileRepositoryMock = Mockito.mock(PatientInfoFileRepository.class);
        Mockito.when(patientInfoFileRepositoryMock.getById((Mockito.anyString())))
                .thenReturn(patientInfo);

        String expected = String.format("Warning, patient with id: %s, need help", patientInfo.getId());

        SendAlertService alertServiceMock = Mockito.mock(SendAlertServiceImpl.class);

        MedicalService medicalService = new MedicalServiceImpl(patientInfoFileRepositoryMock, alertServiceMock);
        medicalService.checkBloodPressure(patientInfo.getId(), new BloodPressure(140, 100));

        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(alertServiceMock).send(argumentCaptor.capture());

        Assertions.assertEquals(expected, argumentCaptor.getValue());
    }

    @Test
    void checkTemperature() {
        PatientInfo patientInfo = new PatientInfo(UUID.randomUUID().toString(),
                "Иван", "Иванов", LocalDate.of(1969, 12, 31),
                new HealthInfo(new BigDecimal("36.6"), new BloodPressure(120, 80)));
        PatientInfoFileRepository patientInfoFileRepositoryMock = Mockito.mock(PatientInfoFileRepository.class);
        Mockito.when(patientInfoFileRepositoryMock.getById((Mockito.anyString())))
                .thenReturn(patientInfo);

        String expected = String.format("Warning, patient with id: %s, need help", patientInfo.getId());

        SendAlertService alertServiceMock = Mockito.mock(SendAlertServiceImpl.class);

        MedicalService medicalService = new MedicalServiceImpl(patientInfoFileRepositoryMock, alertServiceMock);
        medicalService.checkTemperature(patientInfo.getId(), new BigDecimal("35.0"));

        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);

        Mockito.verify(alertServiceMock).send(argumentCaptor.capture());
        Assertions.assertEquals(expected, argumentCaptor.getValue());

    }

    @Test
    void testSendAlert() {
        PatientInfo patientInfo = new PatientInfo(UUID.randomUUID().toString(),
                "Иван", "Иванов", LocalDate.of(1969, 12, 31),
                new HealthInfo(new BigDecimal("36.6"), new BloodPressure(120, 80)));
        String expected = String.format("Warning, patient with id: %s, need help", patientInfo.getId());

        PatientInfoFileRepository patientInfoFileRepository = Mockito.mock(PatientInfoFileRepository.class);
        Mockito.when(patientInfoFileRepository.getById(patientInfo.getId()))
                .thenReturn(patientInfo);

        SendAlertServiceImpl sendAlertService = Mockito.spy(SendAlertServiceImpl.class);

        MedicalService medicalService = new MedicalServiceImpl(patientInfoFileRepository, sendAlertService);
        medicalService.checkTemperature(patientInfo.getId(), new BigDecimal("36.0"));
        medicalService.checkBloodPressure(patientInfo.getId(), new BloodPressure(120, 80));

        Mockito.verify(sendAlertService, Mockito.times(0)).send(expected);

    }
}